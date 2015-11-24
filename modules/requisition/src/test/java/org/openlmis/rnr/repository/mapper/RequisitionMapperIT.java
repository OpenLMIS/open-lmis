/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.name;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramBuilder.programName;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.core.builder.UserBuilder.active;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.domain.RightName.CONVERT_TO_ORDER;
import static org.openlmis.core.domain.RightType.FULFILLMENT;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RequisitionMapperIT {
  public static final Long MODIFIED_BY = 1L;
  public static final Long USER_ID = 2L;
  @Autowired
  LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  SupplyLineMapper supplyLineMapper;
  @Autowired
  RequisitionStatusChangeMapper requisitionStatusChangeMapper;
  private Facility facility;
  private ProcessingSchedule processingSchedule;
  private ProcessingPeriod processingPeriod1;
  private ProcessingPeriod processingPeriod2;
  private ProcessingPeriod processingPeriod3;
  private Program program;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private RoleRightsMapper roleRightsMapper;
  @Autowired
  private QueryExecutor queryExecutor;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RequisitionMapper mapper;
  @Autowired
  private RnrLineItemMapper lineItemMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;
  private SupervisoryNode supervisoryNode;
  private Role role;
  private Date modifiedDate;
  private ProductCategory productCategory;
  @Autowired
  private SignatureMapper signatureMapper;

  @Before
  public void setUp() {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    insertProgram();

    Date periodStartDate = new Date();
    Date periodEndDate = new Date();
    processingPeriod1 = insertPeriod("Period 1", processingSchedule, periodStartDate, periodEndDate);
    processingPeriod2 = insertPeriod("Period 2", processingSchedule, periodStartDate, periodEndDate);
    processingPeriod3 = insertPeriod("Period 3", processingSchedule, periodStartDate, periodEndDate);
    supervisoryNode = insertSupervisoryNode("N1");
    insertSupplyLine(facility, supervisoryNode);
    role = insertRole();
    modifiedDate = new Date();

    productCategory = new ProductCategory("C1", "Category 1", 1);
    productCategoryMapper.insert(productCategory);

  }

  @Test
  public void shouldSetRequisitionId() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetRequisitionById() {
    Rnr requisition = new Rnr(new Facility(facility.getId()), new Program(program.getId()), processingPeriod1, false, MODIFIED_BY, 1L);
    requisition.setAllocatedBudget(new BigDecimal(123.45));
    requisition.setStatus(INITIATED);
    requisition.setId(1L);

    String submitterText = "submitter";
    Signature submitterSignature = new Signature(Signature.Type.SUBMITTER, submitterText);
    String approverText = "approver";
    Signature approverSignature = new Signature(Signature.Type.APPROVER, approverText);

    ArrayList<Signature> rnrSignatures = new ArrayList<>();
    rnrSignatures.add(submitterSignature);
    rnrSignatures.add(approverSignature);

    requisition.setRnrSignatures(rnrSignatures);

    signatureMapper.insertSignature(submitterSignature);
    signatureMapper.insertSignature(approverSignature);
    mapper.insert(requisition);
    mapper.insertRnrSignature(requisition, submitterSignature);
    mapper.insertRnrSignature(requisition, approverSignature);

    Product product = insertProduct(true, "P1");
    RnrLineItem fullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product.getCode())));
    RnrLineItem nonFullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product.getCode())));
    fullSupplyLineItem.setRnrId(requisition.getId());
    nonFullSupplyLineItem.setRnrId(requisition.getId());
    lineItemMapper.insert(fullSupplyLineItem, Collections.EMPTY_LIST.toString());
    lineItemMapper.insert(nonFullSupplyLineItem, Collections.EMPTY_LIST.toString());

    ProgramProduct programProduct = new ProgramProduct(program, product, 1, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);

    User author = new User();
    author.setId(1L);
    Comment comment = new Comment(requisition.getId(), author, "A comment", null);
    commentMapper.insert(comment);
    updateSupplyingDepotForRequisition(requisition);

    Rnr fetchedRequisition = mapper.getById(requisition.getId());

    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgram().getId(), is(equalTo(program.getId())));
    assertThat(fetchedRequisition.getFacility().getId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriod().getId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
    assertThat(fetchedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(fetchedRequisition.getNonFullSupplyLineItems().size(), is(1));
    assertThat(fetchedRequisition.getAllocatedBudget(), is(new BigDecimal(123.45).setScale(2, RoundingMode.FLOOR)));
    assertThat(fetchedRequisition.getRnrSignatures().size(), is(2));
    assertThat(fetchedRequisition.getRnrSignatures().get(0).getType(), is(Signature.Type.SUBMITTER));
    assertThat(fetchedRequisition.getRnrSignatures().get(0).getText(), is(submitterText));
    assertThat(fetchedRequisition.getRnrSignatures().get(1).getType(), is(Signature.Type.APPROVER));
    assertThat(fetchedRequisition.getRnrSignatures().get(1).getText(), is(approverText));
  }

  @Test
  public void shouldGetRequisitionsByFacilityAndProgram() {
    String facilityCode = "F10";
    String programCode = "MMIA";

    Facility queryFacility = new Facility(facility.getId());
    queryFacility.setCode(facilityCode);

    Program queryProgram = new Program(program.getId());
    queryProgram.setCode(programCode);

    Rnr requisition = new Rnr(queryFacility, queryProgram, processingPeriod1, false, MODIFIED_BY, 4L);
    requisition.setAllocatedBudget(new BigDecimal(123.45));
    requisition.setStatus(INITIATED);

    mapper.insert(requisition);

    List<Rnr> rnrList = mapper.getRequisitionsWithLineItemsByFacility(queryFacility);
    assertThat(rnrList.size(), is(1));

    Facility anotherFacility = new Facility(122L);
    facility.setCode(FACILITY_CODE);

    rnrList = mapper.getRequisitionsWithLineItemsByFacility(anotherFacility);
    assertThat(rnrList.size(), is(0));
  }

  @Test
  public void shouldUpdateRequisition() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    requisition.setModifiedBy(USER_ID);
    Date submittedDate = new Date();
    requisition.setSubmittedDate(submittedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    requisition.setSupplyingDepot(facility);

    mapper.update(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getSupervisoryNodeId(), is(requisition.getSupervisoryNodeId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldUpdateClientSubmittedTime() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    Date clientSubmittedTime = new Date();
    requisition.setClientSubmittedTime(clientSubmittedTime);

    mapper.updateClientFields(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getClientSubmittedTime(), is(clientSubmittedTime));

  }

  @Test
  public void shouldUpdateClientSubmittedNotes() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    requisition.setClientSubmittedNotes("xyz");

    mapper.updateClientFields(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertEquals("xyz", updatedRequisition.getClientSubmittedNotes());
  }

  @Test
  public void shouldReturnRequisitionWithLineItemsByFacilityProgramAndPeriod() {
    Product fullSupplyProduct = insertProduct(true, "P1");
    Product nonFullSupplyProduct = insertProduct(false, "P2");

    ProgramProduct fullSupplyProgramProduct = insertProgramProductWithProductCategory(fullSupplyProduct, program);
    ProgramProduct nonFullSupplyProgramProduct = insertProgramProductWithProductCategory(nonFullSupplyProduct, program);

    FacilityTypeApprovedProduct fullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(fullSupplyProgramProduct);
    FacilityTypeApprovedProduct nonFullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(nonFullSupplyProgramProduct);

    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod2, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    insertRnrLineItem(requisition, fullSupplyFacilityTypeApprovedProduct);
    insertRnrLineItem(requisition, nonFullSupplyFacilityTypeApprovedProduct);

    Rnr returnedRequisition = mapper.getRequisitionWithLineItems(facility, new Program(program.getId()), processingPeriod1);

    assertThat(returnedRequisition.getId(), is(requisition.getId()));
    assertThat(returnedRequisition.getFacility().getId(), is(facility.getId()));
    assertThat(returnedRequisition.getProgram().getId(), is(program.getId()));
    assertThat(returnedRequisition.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(returnedRequisition.getNonFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldReturnRequisitionWithoutLineItemsByFacilityProgramAndPeriod() {
    Product fullSupplyProduct = insertProduct(true, "P1");
    Product nonFullSupplyProduct = insertProduct(false, "P2");

    ProgramProduct fullSupplyProgramProduct = insertProgramProductWithProductCategory(fullSupplyProduct, program);
    ProgramProduct nonFullSupplyProgramProduct = insertProgramProductWithProductCategory(nonFullSupplyProduct, program);

    FacilityTypeApprovedProduct fullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(fullSupplyProgramProduct);
    FacilityTypeApprovedProduct nonFullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(nonFullSupplyProgramProduct);

    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod2, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    insertRnrLineItem(requisition, fullSupplyFacilityTypeApprovedProduct);
    insertRnrLineItem(requisition, nonFullSupplyFacilityTypeApprovedProduct);

    Rnr returnedRequisition = mapper.getRequisitionWithoutLineItems(facility.getId(), program.getId(), processingPeriod1.getId());

    assertThat(returnedRequisition.getId(), is(requisition.getId()));
    assertThat(returnedRequisition.getFacility().getId(), is(facility.getId()));
    assertThat(returnedRequisition.getProgram().getId(), is(program.getId()));
    assertThat(returnedRequisition.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(0));
    assertThat(returnedRequisition.getNonFullSupplyLineItems().size(), is(0));
  }

  @Test
  public void shouldGetOnlyRegularRequisitions() throws Exception {
    Rnr regularRnr = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod1, program, INITIATED, true, facility, supervisoryNode, modifiedDate);

    Rnr regularRequisition = mapper.getRegularRequisitionWithLineItems(facility, program, processingPeriod1);

    assertThat(regularRequisition.getId(), is(regularRnr.getId()));
  }

  @Test
  public void shouldPopulateLineItemsWhenGettingRnrById() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    Product product = insertProduct(true, "P1");
    ProgramProduct programProduct = insertProgramProductWithProductCategory(product, program);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = insertFacilityApprovedProduct(programProduct);

    RnrLineItem item1 = insertRnrLineItem(requisition, facilityTypeApprovedProduct);
    lossesAndAdjustmentsMapper.insert(item1, RnrLineItemBuilder.ONE_LOSS);
    Rnr returnedRequisition = mapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(1));
    final RnrLineItem item = returnedRequisition.getFullSupplyLineItems().get(0);
    assertThat(item.getLossesAndAdjustments().size(), is(1));
    assertThat(returnedRequisition.getFacility().getId(), is(requisition.getFacility().getId()));
    assertThat(returnedRequisition.getStatus(), is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(), is(requisition.getId()));
  }

  @Test
  public void shouldNotGetInitiatedRequisitionsForFacilitiesAndPrograms() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(null);

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForRoleAssignment() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, AUTHORIZED, true, facility, supervisoryNode, modifiedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);
    RoleAssignment roleAssignment = new RoleAssignment(USER_ID, 1L, program.getId(), supervisoryNode);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(roleAssignment);

    Rnr rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getFacility().getId(), is(facility.getId()));
    assertThat(rnr.getProgram().getId(), is(program.getId()));
    assertThat(rnr.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getModifiedDate(), is(notNullValue()));
    assertTrue(rnr.isEmergency());
  }

  @Test
  public void shouldGetTheLastRegularRequisitionToEnterThePostSubmitFlow() throws Exception {
    DateTime date1 = now();
    DateTime date2 = date1.plusMonths(1);

    ProcessingPeriod processingPeriod4 = make(a(defaultProcessingPeriod,
        with(scheduleId, processingSchedule.getId()),
        with(ProcessingPeriodBuilder.name, "Period4")));
    processingPeriod4.setStartDate(new Date());

    processingPeriodMapper.insert(processingPeriod4);

    Rnr rnr1 = insertRequisition(processingPeriod1, program, AUTHORIZED, false, facility, supervisoryNode, modifiedDate);
    rnr1.setSubmittedDate(date1.toDate());
    mapper.update(rnr1);

    Rnr rnr2 = insertRequisition(processingPeriod4, program, APPROVED, false, facility, supervisoryNode, modifiedDate);
    rnr2.setSubmittedDate(date2.toDate());
    mapper.update(rnr2);

    insertRequisition(processingPeriod3, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    Rnr lastRequisitionToEnterThePostSubmitFlow = mapper.getLastRegularRequisitionToEnterThePostSubmitFlow(facility.getId(), program.getId());

    assertThat(lastRequisitionToEnterThePostSubmitFlow.getId(), is(rnr2.getId()));
  }

  @Test
  public void shouldNotGetEmergencyRequisitionsForPostSubmitFlow() throws Exception {
    insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod1, program, AUTHORIZED, true, facility, supervisoryNode, modifiedDate);

    Rnr lastRequisition = mapper.getLastRegularRequisitionToEnterThePostSubmitFlow(facility.getId(), program.getId());

    assertThat(lastRequisition, is(nullValue()));
  }

  @Test
  public void shouldGetLastRegularRequisitionForFacilityAndProgram() throws Exception {
    Rnr expectedRnr = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    Rnr lastRequisition = mapper.getLastRegularRequisition(facility, program);

    assertThat(lastRequisition.getStatus(), is(expectedRnr.getStatus()));
    assertThat(lastRequisition.getPeriod().getId(), is(processingPeriod1.getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByFacilityCode() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);

    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_FACILITY_CODE;
    Integer pageNumber = 1;
    Integer pageSize = 2;

    String sortDirection = "asc";
    String sortBy = "submittedDate";
    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "F10", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByFacilityName() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);

    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_FACILITY_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "submittedDate";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "Apollo", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingBySupplyDepotName() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);

    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_SUPPLYING_DEPOT_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "submittedDate";
    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "apollo", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByProgramName() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_PROGRAM_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "submittedDate";
    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "Yellow", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetRequisitionsForViewByFacilityProgramAndPeriodIds() throws Exception {
    String commaSeparatedPeriodIds = "{" + processingPeriod1.getId() + "," + processingPeriod2.getId() + "," + processingPeriod3.getId() + "}";
    insertRequisition(processingPeriod1, program, AUTHORIZED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod2, program, APPROVED, false, facility, supervisoryNode, modifiedDate);
    insertRequisition(processingPeriod3, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    List<Rnr> result = mapper.getPostSubmitRequisitions(facility, program, commaSeparatedPeriodIds);
    assertThat(result.size(), is(2));
  }

  @Test
  public void shouldOnlyLoadEmergencyRequisitionDataForGivenQuery() throws Exception {
    Rnr initiatedRequisition = insertRequisition(processingPeriod1, program, INITIATED, true, facility, supervisoryNode, modifiedDate);
    Rnr submittedRequisition = insertRequisition(processingPeriod1, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);

    List<Rnr> actualRequisitions =
      mapper.getInitiatedOrSubmittedEmergencyRequisitions(facility.getId(), program.getId());

    assertContainsRequisition(actualRequisitions, initiatedRequisition);
    assertContainsRequisition(actualRequisitions, submittedRequisition);
  }

  private void assertContainsRequisition(List<Rnr> actualRequisitions, final Rnr expectedRequisition) {
    assertTrue(CollectionUtils.exists(actualRequisitions, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        Rnr requisition = (Rnr) o;
        return requisition.getStatus().equals(expectedRequisition.getStatus()) && requisition.getId().equals(expectedRequisition.getId());
      }
    }));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfProgramName() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Program program1 = make(a(ProgramBuilder.defaultProgram, with(programCode, "YF"), with(programName, "Yellow fever")));
    programMapper.insert(program1);
    Program program2 = make(a(ProgramBuilder.defaultProgram, with(programCode, "GF"), with(programName, "Green fever")));
    programMapper.insert(program2);

    Rnr requisition1 = insertRequisition(processingPeriod1, program1, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod3, program2, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "programName";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getProgram().getId(), is(requisition2.getProgram().getId()));
    assertThat(requisitions.get(1).getProgram().getId(), is(requisition1.getProgram().getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfFacilityName() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(name, "village pharmacy"), with(FacilityBuilder.code, "VP")));
    facilityMapper.insert(facility1);
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(name, "central hospital"), with(FacilityBuilder.code, "CH")));
    facilityMapper.insert(facility2);
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility1, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility2, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "facilityName";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getFacility().getId(), is(requisition2.getFacility().getId()));
    assertThat(requisitions.get(1).getFacility().getId(), is(requisition1.getFacility().getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfFacilityCode() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(name, "village pharmacy"), with(FacilityBuilder.code, "VP")));
    facilityMapper.insert(facility1);
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(name, "central hospital"), with(FacilityBuilder.code, "CH")));
    facilityMapper.insert(facility2);
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility1, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility2, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "facilityCode";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getFacility().getId(), is(requisition2.getFacility().getId()));
    assertThat(requisitions.get(1).getFacility().getId(), is(requisition1.getFacility().getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfSupplyingDepotName() throws SQLException {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(name, "village pharmacy"), with(FacilityBuilder.code, "VP")));
    facilityMapper.insert(facility1);
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(name, "central hospital"), with(FacilityBuilder.code, "CH")));
    facilityMapper.insert(facility2);
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility1.getId(), userId);
    insertRoleForApprovedRequisitions(facility2.getId(), userId);

    SupervisoryNode supervisoryNode1 = insertSupervisoryNode("N2");
    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N3");

    insertSupplyLine(facility1, supervisoryNode1);
    insertSupplyLine(facility2, supervisoryNode2);

    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility1, supervisoryNode1, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod3, program, SUBMITTED, false, facility2, supervisoryNode2, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "supplyingDepotName";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getFacility().getId(), is(requisition2.getFacility().getId()));
    assertThat(requisitions.get(1).getFacility().getId(), is(requisition1.getFacility().getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfModifiedDate() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Date date1 = new Date();
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false, facility, supervisoryNode, date1);
    Calendar calendar = Calendar.getInstance();
    calendar.add(calendar.DAY_OF_MONTH, 1);
    Date date2 = calendar.getTime();

    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, date2);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "modifiedDate";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getId(), is(requisition1.getId()));
    assertThat(requisitions.get(1).getId(), is(requisition2.getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfEmergency() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2, requisition3);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "emergency";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getId(), is(requisition2.getId()));
    assertThat(requisitions.get(1).getId(), is(requisition1.getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfPeriodStartDate() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);

    ProcessingSchedule processingSchedule1 = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "PS1")));
    processingScheduleMapper.insert(processingSchedule1);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "PS2")));
    processingScheduleMapper.insert(processingSchedule2);

    Date periodStartDate1 = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.add(calendar.DAY_OF_MONTH, 1);
    Date periodStartDate2 = calendar.getTime();

    ProcessingPeriod processingPeriod4 = insertPeriod("period4", processingSchedule1, periodStartDate1, new Date());
    ProcessingPeriod processingPeriod5 = insertPeriod("period5", processingSchedule2, periodStartDate2, new Date());

    Rnr requisition1 = insertRequisition(processingPeriod4, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod5, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "periodStartDate";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getId(), is(requisition1.getId()));
    assertThat(requisitions.get(1).getId(), is(requisition2.getId()));
  }

  @Test
  public void shouldGetApprovedRequisitionsInAscOrderOfPeriodEndDate() throws SQLException {
    Long userId = insertUser();
    insertRoleForApprovedRequisitions(facility.getId(), userId);

    ProcessingSchedule processingSchedule1 = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "PS1")));
    processingScheduleMapper.insert(processingSchedule1);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "PS2")));
    processingScheduleMapper.insert(processingSchedule2);

    Date periodEndDate1 = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.add(calendar.DAY_OF_MONTH, 1);
    Date periodEndDate2 = calendar.getTime();

    ProcessingPeriod processingPeriod4 = insertPeriod("period4", processingSchedule1, new Date(), periodEndDate1);
    ProcessingPeriod processingPeriod5 = insertPeriod("period5", processingSchedule2, new Date(), periodEndDate2);

    Rnr requisition1 = insertRequisition(processingPeriod4, program, SUBMITTED, true, facility, supervisoryNode, modifiedDate);
    Rnr requisition2 = insertRequisition(processingPeriod5, program, SUBMITTED, false, facility, supervisoryNode, modifiedDate);

    approve(requisition1, requisition2);

    Integer pageNumber = 1;
    Integer pageSize = 2;
    String sortDirection = "asc";
    String sortBy = "periodEndDate";

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber("", "", pageNumber,
      pageSize, userId, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(2));

    assertThat(requisitions.get(0).getId(), is(requisition1.getId()));
    assertThat(requisitions.get(1).getId(), is(requisition2.getId()));
  }

  @Test
  public void shouldGetLWRequisitionById() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);
    Product product = insertProduct(true, "P1");
    RnrLineItem fullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product.getCode())));
    RnrLineItem nonFullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product.getCode())));
    fullSupplyLineItem.setRnrId(requisition.getId());
    nonFullSupplyLineItem.setRnrId(requisition.getId());
    lineItemMapper.insert(fullSupplyLineItem, null);
    lineItemMapper.insert(nonFullSupplyLineItem, null);

    User author = new User();
    author.setId(1L);
    Comment comment = new Comment(requisition.getId(), author, "A comment", null);
    commentMapper.insert(comment);

    Rnr fetchedRequisition = mapper.getLWById(requisition.getId());

    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgram().getId(), is(equalTo(program.getId())));
    assertThat(fetchedRequisition.getFacility().getId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriod().getId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
    assertThat(fetchedRequisition.getFullSupplyLineItems().size(), is(0));
    assertThat(fetchedRequisition.getNonFullSupplyLineItems().size(), is(0));
    assertThat(fetchedRequisition.getRegimenLineItems().size(), is(0));
  }

  @Test
  public void shouldGetFacilityIdGivenARnrId() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    assertThat(mapper.getFacilityId(requisition.getId()), is(requisition.getFacility().getId()));
  }

  @Test
  public void shouldGetProgramIdGivenRnrId() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    assertThat(mapper.getProgramId(requisition.getId()), is(requisition.getProgram().getId()));
  }

  @Test
  public void shouldInsertRnrSignatures() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    Signature submitterSignature = new Signature(Signature.Type.SUBMITTER, "Mystique");

    signatureMapper.insertSignature(submitterSignature);
    mapper.insertRnrSignature(requisition, submitterSignature);

    List<Signature> dbRnrSignatures = mapper.getRnrSignaturesByRnrId(requisition.getId());
    assertThat(dbRnrSignatures.size(), is(1));
    assertThat(dbRnrSignatures.get(0).getText(), is("Mystique"));
  }

  @Test
  public void shouldReturnSignaturesInRequisitions() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false, facility, supervisoryNode, modifiedDate);

    Signature submitterSignature = new Signature(Signature.Type.SUBMITTER, "Mystique");

    signatureMapper.insertSignature(submitterSignature);
    mapper.insertRnrSignature(requisition, submitterSignature);

    List<Rnr> rnrs = mapper.getRequisitionsWithLineItemsByFacility(facility);
    List<Signature> rnrSignatures = rnrs.get(0).getRnrSignatures();

    assertThat(rnrSignatures.size(), is(1));
    assertThat(rnrSignatures.get(0).getText(), is("Mystique"));
  }

  private void insertRoleForApprovedRequisitions(Long facilityId, Long userId) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId,facilityId,roleId) values (?,?,?)", userId, facilityId, role.getId());
  }

  private Role insertRole() {
    Right right1 = new Right(CONVERT_TO_ORDER, FULFILLMENT);
    Right right2 = new Right(RightName.VIEW_ORDER, FULFILLMENT);
    Role role = new Role("r1", "random description", asList(right1,right2));
    Long roleId = Long.valueOf(roleRightsMapper.insertRole(role));
    role.setId(roleId);
    for (Right right : role.getRights()) {
      roleRightsMapper.createRoleRight(role, right.getName());
    }
    return role;
  }

  private Long insertUser() {
    Long userId = 1l;

    User someUser = make(a(defaultUser, with(UserBuilder.facilityId, facility.getId()), with(active, true)));
    userMapper.insert(someUser);
    return userId;
  }

  private void populateProgramValuesForComparison(Rnr requisition, int index, List<Rnr> requisitions) {
    requisition.setSubmittedDate(requisitions.get(index).getSubmittedDate());
    requisition.setProgram(requisitions.get(index).getProgram());
    requisition.setSupplyingDepot(requisitions.get(index).getSupplyingDepot());
    requisition.setSupervisoryNodeId(requisitions.get(index).getSupervisoryNodeId());
  }

  private Rnr insertRequisition(ProcessingPeriod period, Program program, RnrStatus status, Boolean emergency, Facility facility, SupervisoryNode supervisoryNode, Date modifiedDate) {
    Rnr rnr = new Rnr(new Facility(facility.getId()), new Program(program.getId()), new ProcessingPeriod(period.getId()), emergency, MODIFIED_BY, 1L);
    rnr.setStatus(status);
    rnr.setEmergency(emergency);
    rnr.setModifiedDate(modifiedDate);
    rnr.setSubmittedDate(new Date(111111L));
    rnr.setProgram(program);
    rnr.setSupplyingDepot(facility);
    mapper.insert(rnr);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    rnr.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(rnr);

    return rnr;
  }

  private RnrLineItem insertRnrLineItem(Rnr rnr, FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    RnrLineItem item = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, 1L, 1L);
    lineItemMapper.insert(item, Collections.EMPTY_LIST.toString());
    return item;
  }

  private FacilityTypeApprovedProduct insertFacilityApprovedProduct(ProgramProduct programProduct) {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(FacilityApprovedProductBuilder.defaultFacilityApprovedProduct));
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);
    return facilityTypeApprovedProduct;
  }

  private ProgramProduct insertProgramProductWithProductCategory(Product product, Program program) {
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
    programProduct.setProductCategory(productCategory);
    programProduct.setFullSupply(product.getFullSupply());
    programProductMapper.insert(programProduct);
    return programProduct;
  }

  private void insertProgram() {
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  private Product insertProduct(boolean isFullSupply, String productCode) {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode), with(ProductBuilder.fullSupply, isFullSupply)));
    productMapper.insert(product);
    return product;
  }

  private ProcessingPeriod insertPeriod(String name, ProcessingSchedule processingSchedule, Date periodStartDate, Date periodEndDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()), with(startDate, periodStartDate), with(endDate, periodEndDate),
      with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode(String code) {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.code, code)));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  private void insertSupplyLine(Facility facility, SupervisoryNode supervisoryNode) {
    SupplyLine supplyLine = make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, facility),
      with(SupplyLineBuilder.supervisoryNode, supervisoryNode), with(SupplyLineBuilder.program, program)));
    supplyLineMapper.insert(supplyLine);
  }

  private void updateSupplyingDepotForRequisition(Rnr requisition) {
    requisition.setSupplyingDepot(facility);
    mapper.update(requisition);
  }

  private void approve(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      requisition.setStatus(APPROVED);
      mapper.update(requisition);
    }
  }
}
