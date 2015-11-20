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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RightType;
import org.openlmis.core.domain.Role;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RightMapperIT {

  @Autowired
  RightMapper rightMapper;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Test
  public void shouldInsertRight() throws SQLException {

    Right right = new Right();
    right.setName("Requisition Group Program");
    right.setType(RightType.REPORTING);

    rightMapper.insertRight(right);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM rights WHERE name = ?", right.getName());
    resultSet.next();
    assertThat(resultSet.getString("name"), is(right.getName()));
    assertThat(resultSet.getString("rightType"), is(right.getType().toString()));
    assertNotNull(resultSet.getTimestamp("createdDate"));
  }

  @Test
  public void shouldReturnCountOfReportingRightOfUser() throws SQLException {
    Long userId = 1L;
    Right right1 = new Right("template1", RightType.REPORTING);
    Right right2 = new Right("template2", RightType.REPORTING);
    Right right3 = new Right("template3", RightType.REPORTING);
    Right right4 = new Right("admin", RightType.REPORTING);
    rightMapper.insertRight(right1);
    rightMapper.insertRight(right2);
    rightMapper.insertRight(right3);
    rightMapper.insertRight(right4);

    Role role = new Role();
    role.setName("New Role");
    roleRightsMapper.insertRole(role);
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), "template1");
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), "template2");
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), "template3");
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), "admin");

    queryExecutor.executeUpdate("INSERT INTO role_assignments(userId, roleId) VALUES (?,?)", userId, role.getId());

    Integer count = rightMapper.totalReportingRightsFor(userId);

    assertThat(count, is(4));
  }

  @Test
  public void shouldGetAllRights() {

    List<Right> rights = rightMapper.getAll();

    assertThat(rights.size(), is(97));
    assertThat(rights.get(0).getDisplayOrder(),is(1));
  }
}