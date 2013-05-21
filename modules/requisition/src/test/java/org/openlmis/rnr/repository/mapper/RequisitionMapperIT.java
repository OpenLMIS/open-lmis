/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

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
import org.openlmis.rnr.domain.Comment;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.startDate;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RequisitionMapperIT {
  public static final Long MODIFIED_BY = 1L;
  public static final Long PROGRAM_ID = 1L;
  public static final Long USER_ID = 2L;

  private Facility facility;
  private ProcessingSchedule processingSchedule;
  private ProcessingPeriod processingPeriod1;
  private ProcessingPeriod processingPeriod2;
  private ProcessingPeriod processingPeriod3;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RequisitionMapper mapper;
  @Autowired
  RnrLineItemMapper lineItemMapper;
  @Autowired
  LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
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

  private SupervisoryNode supervisoryNode;


  @Before
  public void setUp() {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod1 = insertPeriod("Period 1");
    processingPeriod2 = insertPeriod("Period 2");
    processingPeriod3 = insertPeriod("Period 3");
    supervisoryNode = insertSupervisoryNode();
  }

  @Test
  public void shouldSetRequisitionId() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetRequisitionById() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    Product product = insertProduct(true, "P1");
    RnrLineItem fullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product.getCode())));
    RnrLineItem nonFullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product.getCode())));
    fullSupplyLineItem.setRnrId(requisition.getId());
    nonFullSupplyLineItem.setRnrId(requisition.getId());
    lineItemMapper.insert(fullSupplyLineItem);
    lineItemMapper.insert(nonFullSupplyLineItem);

    User author = new User();
    author.setId(1L);
    Comment comment = new Comment(requisition.getId(), author, "A comment", null);
    commentMapper.insert(comment);

    Rnr fetchedRequisition = mapper.getById(requisition.getId());

    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgram().getId(), is(equalTo(PROGRAM_ID)));
    assertThat(fetchedRequisition.getFacility().getId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriod().getId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
    assertThat(fetchedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(fetchedRequisition.getNonFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldUpdateRequisition() {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    Facility supplyingFacility = make(a(defaultFacility, with(code, "SF")));
    facilityMapper.insert(supplyingFacility);
    requisition.setModifiedBy(USER_ID);
    Date submittedDate = new Date();
    requisition.setSubmittedDate(submittedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    requisition.setSupplyingFacility(supplyingFacility);

    mapper.update(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getSupervisoryNodeId(), is(requisition.getSupervisoryNodeId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_ID)));
    assertThat(updatedRequisition.getSubmittedDate(), is(submittedDate));
    assertThat(updatedRequisition.getSupplyingFacility().getId(), is(supplyingFacility.getId()));
  }


  @Test
  public void shouldReturnRequisitionWithoutLineItemsByFacilityProgramAndPeriod() {
    Program program = insertProgram();

    Product fullSupplyProduct = insertProduct(true, "P1");
    Product nonFullSupplyProduct = insertProduct(false, "P2");

    ProgramProduct fullSupplyProgramProduct = insertProgramProduct(fullSupplyProduct, program);
    ProgramProduct nonFullSupplyProgramProduct = insertProgramProduct(nonFullSupplyProduct, program);

    FacilityApprovedProduct fullSupplyFacilityApprovedProduct = insertFacilityApprovedProduct(fullSupplyProgramProduct);
    FacilityApprovedProduct nonFullSupplyFacilityApprovedProduct = insertFacilityApprovedProduct(nonFullSupplyProgramProduct);

    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    insertRequisition(processingPeriod2, INITIATED);

    insertRnrLineItem(requisition, fullSupplyFacilityApprovedProduct);
    insertRnrLineItem(requisition, nonFullSupplyFacilityApprovedProduct);

    Rnr returnedRequisition = mapper.getRequisitionWithLineItems(facility, new Program(PROGRAM_ID), processingPeriod1);

    assertThat(returnedRequisition.getId(), is(requisition.getId()));
    assertThat(returnedRequisition.getFacility().getId(), is(facility.getId()));
    assertThat(returnedRequisition.getProgram().getId(), is(PROGRAM_ID));
    assertThat(returnedRequisition.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(returnedRequisition.getNonFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldPopulateLineItemsWhenGettingRnrById() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    Product product = insertProduct(true, "P1");
    Program program = insertProgram();
    ProgramProduct programProduct = insertProgramProduct(product, program);
    FacilityApprovedProduct facilityApprovedProduct = insertFacilityApprovedProduct(programProduct);

    RnrLineItem item1 = insertRnrLineItem(requisition, facilityApprovedProduct);
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
    Rnr requisition = insertRequisition(processingPeriod1, INITIATED);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(null);

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForRoleAssignment() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, AUTHORIZED);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);
    RoleAssignment roleAssignment = new RoleAssignment(USER_ID, 1L, PROGRAM_ID, supervisoryNode);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(roleAssignment);

    Rnr rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getFacility().getId(), is(facility.getId()));
    assertThat(rnr.getProgram().getId(), is(PROGRAM_ID));
    assertThat(rnr.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getModifiedDate(), is(notNullValue()));
    assertThat(rnr.getSubmittedDate(), is(requisition.getSubmittedDate()));
  }

  @Test
  public void shouldGetTheLastRequisitionToEnterThePostSubmitFlow() throws Exception {
    DateTime date1 = now();
    DateTime date2 = date1.plusMonths(1);

    ProcessingPeriod processingPeriod4 = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period4")));
    processingPeriod4.setStartDate(new Date());

    processingPeriodMapper.insert(processingPeriod4);

    Rnr rnr1 = insertRequisition(processingPeriod1, AUTHORIZED);
    rnr1.setSubmittedDate(date1.toDate());
    mapper.update(rnr1);

    Rnr rnr2 = insertRequisition(processingPeriod4, APPROVED);
    rnr2.setSubmittedDate(date2.toDate());
    mapper.update(rnr2);

    insertRequisition(processingPeriod3, INITIATED);

    Rnr lastRequisitionToEnterThePostSubmitFlow = mapper.getLastRequisitionToEnterThePostSubmitFlow(facility.getId(), PROGRAM_ID);

    assertThat(lastRequisitionToEnterThePostSubmitFlow.getId(), is(rnr2.getId()));
  }

  @Test
  public void shouldGetAllTheApprovedRequisitions() {
    Rnr requisition = insertRequisition(processingPeriod1, APPROVED);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    Facility supplyingFacility = make(a(defaultFacility, with(code, "SF")));
    facilityMapper.insert(supplyingFacility);
    requisition.setSupplyingFacility(supplyingFacility);
    mapper.update(requisition);

    List<Rnr> requisitions = mapper.getApprovedRequisitions();

    Rnr rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getFacility().getId(), is(facility.getId()));
    assertThat(rnr.getProgram().getId(), is(PROGRAM_ID));
    assertThat(rnr.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getSupplyingFacility().getId(), is(supplyingFacility.getId()));
    assertThat(rnr.getModifiedDate(), is(notNullValue()));
    assertThat(rnr.getSubmittedDate(), is(requisition.getSubmittedDate()));
  }

  @Test
  public void shouldGetRequisitionsForViewByFacilityProgramAndPeriodIds() throws Exception {
    Program program = new Program(PROGRAM_ID);

    String commaSeparatedPeriodIds = "{" + processingPeriod1.getId() + "," + processingPeriod2.getId() + "," + processingPeriod3.getId() + "}";
    insertRequisition(processingPeriod1, AUTHORIZED);
    insertRequisition(processingPeriod2, APPROVED);
    insertRequisition(processingPeriod3, SUBMITTED);
    List<Rnr> result = mapper.get(facility, program, commaSeparatedPeriodIds);
    assertThat(result.size(), is(2));
  }

  private Rnr insertRequisition(ProcessingPeriod period, RnrStatus status) {
    Rnr rnr = new Rnr(facility.getId(), PROGRAM_ID, period.getId(), MODIFIED_BY);
    rnr.setStatus(status);
    rnr.setModifiedDate(new Date());
    rnr.setSubmittedDate(new Date(111111L));
    mapper.insert(rnr);
    return rnr;
  }

  private RnrLineItem insertRnrLineItem(Rnr rnr, FacilityApprovedProduct facilityApprovedProduct) {
    RnrLineItem item = new RnrLineItem(rnr.getId(), facilityApprovedProduct, 1L);
    lineItemMapper.insert(item);
    return item;
  }

  private FacilityApprovedProduct insertFacilityApprovedProduct(ProgramProduct programProduct) {
    FacilityApprovedProduct facilityApprovedProduct = make(a(FacilityApprovedProductBuilder.defaultFacilityApprovedProduct));
    facilityApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProductMapper.insert(facilityApprovedProduct);
    return facilityApprovedProduct;
  }

  private ProgramProduct insertProgramProduct(Product product, Program program) {
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
    programProductMapper.insert(programProduct);
    return programProduct;
  }

  private Program insertProgram() {
    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
    return program;
  }

  private Product insertProduct(boolean isFullSupply, String productCode) {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode), with(ProductBuilder.fullSupply, isFullSupply)));
    productMapper.insert(product);
    return product;
  }

  private ProcessingPeriod insertPeriod(String name) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode() {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }
}
