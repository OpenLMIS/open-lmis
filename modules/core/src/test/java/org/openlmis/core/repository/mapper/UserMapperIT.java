/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Calendar.YEAR;
import static org.apache.commons.lang.time.DateUtils.truncate;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.core.domain.RightName.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class UserMapperIT {

  @Autowired
  UserMapper userMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;
  @Autowired
  private RoleRightsMapper roleRightsMapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  private FulfillmentRoleAssignmentMapper fulfillmentRoleAssignmentMapper;
  @Autowired
  QueryExecutor queryExecutor;

  private Facility facility;
  private SupervisoryNode supervisoryNode;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
  }

  @Test
  public void shouldGetUserByUserNameAndPassword() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true), with(restrictLogin, true)));
    userMapper.insert(someUser);
    userMapper.updateUserPasswordAndVerify(someUser.getId(), "random");

    User user = userMapper.selectUserByUserNameAndPassword(defaultUserName, "random");
    assertThat(user, is(notNullValue()));
    assertThat(user.getUserName(), is(defaultUserName));
    assertThat(user.getId(), is(someUser.getId()));
    assertThat(user.getRestrictLogin(), is(true));
    User user1 = userMapper.selectUserByUserNameAndPassword(defaultUserName, "wrongPassword");
    assertThat(user1, is(nullValue()));
    User user2 = userMapper.selectUserByUserNameAndPassword("wrongUserName", defaultPassword);
    assertThat(user2, is(nullValue()));
  }

  @Test
  public void shouldInsertUserWithDbDefaultDateWhenSuppliedModifiedDateNull() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true)));
    someUser.setModifiedDate(null);

    userMapper.insert(someUser);

    User fetchedUser = userMapper.getByUserName(someUser.getUserName());

    assertThat(fetchedUser, is(notNullValue()));
    assertThat(fetchedUser.getId(), is(someUser.getId()));
    assertThat(fetchedUser.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldInsertUserWithRestrictLoginFLag() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true), with(restrictLogin, true)));
    someUser.setModifiedDate(null);

    userMapper.insert(someUser);

    User fetchedUser = userMapper.getByUserName(someUser.getUserName());

    assertThat(fetchedUser.getRestrictLogin(), is(true));
  }

  @Test
  public void shouldInsertUserWithSuppliedModifiedDate() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true)));
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    someUser.setModifiedDate(calendar.getTime());

    userMapper.insert(someUser);
    User fetchedUser = userMapper.getByUserName(someUser.getUserName());

    assertThat(fetchedUser, is(notNullValue()));
    assertThat(fetchedUser.getId(), is(someUser.getId()));
    assertThat(fetchedUser.getModifiedDate(), is(calendar.getTime()));
  }

  @Test
  public void shouldGetUsersWithGivenRightInNodeForProgram() {
    String nullString = null;
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString), with(active, true)));
    userMapper.insert(someUser);

    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

    Role role = insertRole();
    roleRightsMapper.createRoleRight(role, APPROVE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(someUser.getId(), program.getId(), supervisoryNode.getId(), role.getId());

    final List<User> users = userMapper.getUsersWithRightInNodeForProgram(program, supervisoryNode, APPROVE_REQUISITION);
    someUser.setPassword(null);
    someUser.getSupervisor().setModifiedDate(null);
    someUser.setModifiedDate(null);
    assertThat(users, hasItem(someUser));
  }

  @Test
  public void shouldGetUsersWithGivenRightInNodeHierarchyForProgram() {
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.code, "SN1")));
    supervisoryNode1.setParent(supervisoryNode);
    supervisoryNode1.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode1);

    String nullString = null;
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString), with(active, true)));
    userMapper.insert(someUser);

    User someOtherUser = make(a(defaultUser, with(facilityId, facility.getId()), with(userName, "otherUser"),
      with(email, "email"), with(employeeId, "3")));
    userMapper.insert(someOtherUser);
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

    Role role = insertRole();
    roleRightsMapper.createRoleRight(role, AUTHORIZE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(someUser.getId(), program.getId(), supervisoryNode.getId(), role.getId());
    roleAssignmentMapper.insertRoleAssignment(someOtherUser.getId(), program.getId(), supervisoryNode1.getId(), role.getId());

    final List<User> users = userMapper.getUsersWithRightInHierarchyUsingBaseNode(supervisoryNode1.getId(), program.getId(), AUTHORIZE_REQUISITION);
    someUser.setSupervisor(null);
    someUser.setPassword(null);
    someUser.setModifiedDate(null);

    someOtherUser.setSupervisor(null);
    someOtherUser.setPassword(null);
    someOtherUser.setModifiedDate(null);

    assertThat(users.size(), is(2));
    assertThat(users, hasItem(someUser));
    assertThat(users, hasItem(someOtherUser));
  }

  @Test
  public void shouldReturnNullWhenUserIsVerifiedButDisabled() {
    String nullString = null;
    User user = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString),
      with(verified, true)));
    userMapper.insert(user);

    assertThat(userMapper.selectUserByUserNameAndPassword(user.getUserName(), user.getPassword()), is(nullValue()));
  }

  @Test
  public void shouldInsertAUser() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    Integer userCount = userMapper.insert(user);
    assertThat(userCount, is(1));
    assertThat(user.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetUserWithUserName() throws Exception {
    String nullString = null;

    User user = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString), with(active, true)));
    user.setModifiedDate(Calendar.getInstance().getTime());
    userMapper.insert(user);

    User result = userMapper.getByUserName(user.getUserName());
    user.setPassword(null);
    assertThat(result, is(user));
  }

  @Test
  public void shouldUpdateUserIfUserExist() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    user.setUserName("New Name");

    user.setActive(false);

    userMapper.update(user);

    User fetchedUser = userMapper.getById(user.getId());

    assertThat(fetchedUser.getFirstName(), is(user.getFirstName()));
    assertThat(fetchedUser.getLastName(), is(user.getLastName()));
    assertThat(fetchedUser.getActive(), is(false));
  }

  @Test
  public void shouldGetUserIfUserIdExists() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));

    userMapper.insert(user);

    User returnedUser = userMapper.getById(user.getId());

    assertThat(user.getUserName(), is(returnedUser.getUserName()));
  }

  @Test
  public void shouldGetUserByUserName() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));

    userMapper.insert(user);

    User returnedUser = userMapper.getByUserName(user.getUserName());

    assertThat(user.getUserName(), is(returnedUser.getUserName()));
  }

  @Test
  public void shouldNotGetDisabledUserData() {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    userMapper.disable(user.getId(), 1L);

    assertThat(userMapper.getByUserName(user.getUserName()), is(nullValue()));
  }

  @Test
  public void shouldInsertPasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);

    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(user.getId()));
  }

  @Test
  public void shouldDeletePasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);

    userMapper.deletePasswordResetTokenForUser(user.getId());

    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(nullValue()));
  }

  @Test
  public void shouldGetUserIdForPasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);
    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(user.getId()));
  }

  @Test
  public void shouldUpdateUserPasswordAndActivate() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true)));
    userMapper.insert(user);
    String newPassword = "newPassword";
    userMapper.updateUserPasswordAndVerify(user.getId(), newPassword);
    User returnedUser = userMapper.selectUserByUserNameAndPassword(user.getUserName(), newPassword);
    assertThat(returnedUser, is(notNullValue()));
  }

  @Test
  public void shouldInsertEmailNotification() throws Exception {

    int insertCount = userMapper.insertEmailNotification("toUser@email.com", "subject for email", "content of email");

    assertThat(insertCount, is(1));

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM email_notifications WHERE receiver = ?",
      "toUser@email.com");
    resultSet.next();
    assertThat(resultSet.getString("receiver"), is("toUser@email.com"));
    assertThat(resultSet.getString("subject"), is("subject for email"));
    assertThat(resultSet.getString("content"), is("content of email"));
  }

  @Test
  public void shouldDisableAUser() {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    user.setModifiedDate(truncate(new Date(), YEAR));
    userMapper.insert(user);
    userMapper.disable(user.getId(), 1L);

    User savedUser = userMapper.getByEmail(user.getEmail());
    assertThat(savedUser.getActive(), is(false));
    assertThat(savedUser.getModifiedDate().after(user.getModifiedDate()), is(true));
  }

  @Test
  public void shouldReturnListOfUsersHavingRightOnWarehouse() throws Exception {

    User user1 = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true)));
    userMapper.insert(user1);

    User user2 = make(a(defaultUser, with(facilityId, facility.getId()), with(userName, "user2"),
      with(email, "email"), with(employeeId, "3")));
    userMapper.insert(user2);

    Role role = insertRole();
    roleRightsMapper.createRoleRight(role, CONVERT_TO_ORDER);

    Facility supplyingFacility = make(a(defaultFacility, with(code, "F200")));
    facilityMapper.insert(supplyingFacility);
    fulfillmentRoleAssignmentMapper.insertFulfillmentRole(user1, supplyingFacility.getId(), role.getId());

    List<User> usersWithRight = userMapper.getUsersWithRightOnWarehouse(supplyingFacility.getId(), CONVERT_TO_ORDER);

    assertThat(usersWithRight.size(), is(1));
    assertThat(usersWithRight.get(0).getEmployeeId(), is(user1.getEmployeeId()));

  }

  @Test
  public void shouldGetSearchResultCount(){
    String searchParam = "tim";
    User user1 = createUser("XTom", "timmy", "JFrancis", "j@mail", "e1");
    User user2 =createUser("BTim","franks","JFrancisco","jn@mail","e2");
    User user3 =createUser("CTom","shrank","userTim","ju@mail","e3");
    User user4 =createUser("DTom","nikolas","Nicol","tim@mail","e4");
    User user5 =createUser("ETom","nikolas","Nicole","random@mail","e5");

    userMapper.insert(user1);
    userMapper.insert(user2);
    userMapper.insert(user3);
    userMapper.insert(user4);
    userMapper.insert(user5);

    Integer resultCount = userMapper.getTotalSearchResultCount(searchParam);

    assertThat(resultCount,is(4));
  }

  @Test
  public void shouldGetUsersBySearchParam() {
    String searchParam = "tim";
    Pagination pagination = new Pagination(1, 3);
    User user1 = createUser("XTom", "timmy", "JFrancis", "j@mail", "e1");
    User user2 =createUser("BTim","franks","JFrancisco","jn@mail","e2");
    User user3 =createUser("CTom","shrank","userTim","ju@mail","e3");
    User user4 =createUser("DTom","nikolas","Nicol","tim@mail","e4");
    User user5 =createUser("ETom","nikolas","Nicole","random@mail","e5");

    userMapper.insert(user1);
    userMapper.insert(user2);
    userMapper.insert(user3);
    userMapper.insert(user4);
    userMapper.insert(user5);

    List<User> userList = userMapper.search(searchParam, pagination);

    assertThat(userList.size(), is(3));
    assertThat(userList.get(0).getFirstName(), is(user2.getFirstName()));
    assertThat(userList.get(0).getLastName(), is(user2.getLastName()));
    assertThat(userList.get(0).getUserName(), is(user2.getUserName()));
    assertThat(userList.get(0).getEmail(), is(user2.getEmail()));
    assertThat(userList.get(0).getVerified(), is(user2.getVerified()));
    assertThat(userList.get(0).getActive(), is(user2.getActive()));
    assertThat(userList.get(1).getFirstName(), is(user3.getFirstName()));
    assertThat(userList.get(2).getFirstName(), is(user4.getFirstName()));
  }

  private User createUser(String fName, String lName, String uName, String mail,String empId){
    return make(a(defaultUser,
      with(firstName, fName),
      with(lastName, lName),
      with(userName, uName),
      with(email, mail),
      with(employeeId,empId),
      with(facilityId, facility.getId())));
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRole() {
    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    return r1;
  }

}
