/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.factory.RequisitionFactory;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.modifiedBy;
import static org.openlmis.rnr.builder.RequisitionBuilder.status;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.rnr.service.RequisitionService.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionService.class)
public class RequisitionServiceTest {

  private static final Integer HIV = 1;
  private static final Facility FACILITY = new Facility(1);
  private static final Program PROGRAM = new Program(2);
  private static final ProcessingPeriod PERIOD = make(a(defaultProcessingPeriod, with(id, 10), with(numberOfMonths, 1)));
  private static final Integer USER_ID = 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private RequisitionService requisitionService;

  @Mock
  private FacilityApprovedProductService facilityApprovedProductService;
  @Mock
  private RequisitionRepository requisitionRepository;
  @Mock
  private RnrTemplateRepository rnrTemplateRepository;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RoleAssignmentService roleAssignmentService ;
  @Mock
  private ProgramService programService;
  @Mock
  private ProcessingScheduleService processingScheduleService;
  @Mock
  private FacilityService facilityService;
  @Mock
  private SupplyLineService supplyLineService;
  @Mock
  private RequisitionFactory requisitionFactory;
  @Mock
  private RequisitionPermissionService requisitionPermissionService;

  private Rnr submittedRnr;
  private Rnr initiatedRnr;
  private Rnr authorizedRnr;
  private ArrayList<RnrColumn> rnrColumns;

  @Before
  public void setup() {
    requisitionService = new RequisitionService(requisitionRepository, rnrTemplateRepository, facilityApprovedProductService,
        supervisoryNodeService, roleAssignmentService, programService, processingScheduleService, facilityService, supplyLineService,
        requisitionFactory, requisitionPermissionService);
    submittedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, SUBMITTED), with(modifiedBy, USER_ID)));
    initiatedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, INITIATED), with(modifiedBy, USER_ID)));
    authorizedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, AUTHORIZED), with(modifiedBy, USER_ID)));
    rnrColumns = new ArrayList<RnrColumn>() {{
      add(new RnrColumn());
    }};
  }

  @Test
  public void shouldInitRequisitionAndSetFieldValuesAccordingToTemplate() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);
    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(getRnrColumns());

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(requisition);

    RequisitionService spyRequisitionService = spy(requisitionService);
    doReturn(requisition).when(spyRequisitionService).get(new Facility(FACILITY.getId()), new Program(PROGRAM.getId()), new ProcessingPeriod(PERIOD.getId()));

    Rnr rnr = spyRequisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1);

    verify(facilityApprovedProductService).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).insert(any(Rnr.class));
    verify(requisitionRepository).logStatusChange(any(Rnr.class));

    assertThat(rnr, is(requisition));
    assertThat(requisition.getFullSupplyLineItems().get(0).getQuantityReceived(), is(0));
    assertThat(requisition.getFullSupplyLineItems().get(0).getQuantityDispensed(), is(0));
    assertThat(requisition.getFullSupplyLineItems().get(0).getTotalLossesAndAdjustments(), is(0));
    assertThat(requisition.getFullSupplyLineItems().get(0).getNewPatientCount(), is(0));
    assertThat(requisition.getFullSupplyLineItems().get(0).getStockOutDays(), is(0));
  }

  @Test
  public void shouldInitRequisitionAndSetBeginningBalanceToZeroIfNotVisibleAndPreviousStockInHandNotAvailable() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);

    ArrayList<RnrColumn> rnrColumns = getRnrColumns();
    rnrColumns.add(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE), with(visible, false))));
    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(rnrColumns);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(requisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1);

    assertThat(requisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldInitRequisitionAndNotSetBeginningBalanceToZeroIfVisibleAndPreviousStockInHandNotAvailable() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    requisition.getFullSupplyLineItems().get(0).setBeginningBalance(null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);

    ArrayList<RnrColumn> rnrColumns = getRnrColumns();
    rnrColumns.add(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE), with(visible, true))));
    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(rnrColumns);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(requisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1);

    assertThat(requisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(nullValue()));
  }

  private ArrayList<RnrColumn> getRnrColumns() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, QUANTITY_RECEIVED), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, QUANTITY_DISPENSED), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, LOSSES_AND_ADJUSTMENTS), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, NEW_PATIENT_COUNT), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, STOCK_OUT_DAYS), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, STOCK_IN_HAND), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE), with(visible, true))));
    }};
  }

  @Test
  public void shouldGetRequisition() throws Exception {
    Rnr requisition = spy(new Rnr());
    requisition.setFacility(FACILITY);
    requisition.setProgram(PROGRAM);
    requisition.setPeriod(PERIOD);

    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, PERIOD)).thenReturn(requisition);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    Rnr actualRequisition = requisitionService.get(FACILITY, PROGRAM, PERIOD);

    assertThat(actualRequisition, is(requisition));
    verify(requisition).fillBasicInformation(FACILITY, PROGRAM, PERIOD);
    verify(requisition).fillLastTwoPeriodsNormalizedConsumptions(null, null);
  }


  @Test
  public void shouldGetPreviousTwoRequisitionsNormalizedConsumptionsWhileGettingRequisition() throws Exception {
    final Integer lastPeriodId = 2;
    final int secondLastPeriodsId = 3;
    ProcessingPeriod lastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(id, lastPeriodId)));

    Rnr rnr = new Rnr(FACILITY, PROGRAM, PERIOD);
    final Rnr spyRnr = spy(rnr);

    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, PERIOD)).thenReturn(spyRnr);
    ProcessingPeriod period = new ProcessingPeriod(PERIOD.getId(), PERIOD.getStartDate(), PERIOD.getEndDate(), PERIOD.getNumberOfMonths(), PERIOD.getName());
    when(processingScheduleService.getPeriodById(10)).thenReturn(period);

    when(processingScheduleService.getImmediatePreviousPeriod(period)).thenReturn(lastPeriod);

    ProcessingPeriod secondLastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(id, secondLastPeriodsId)));
    when(processingScheduleService.getImmediatePreviousPeriod(lastPeriod)).thenReturn(secondLastPeriod);

    Rnr lastPeriodsRrn = new Rnr(FACILITY, PROGRAM, lastPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, lastPeriod)).thenReturn(lastPeriodsRrn);

    Rnr secondLastPeriodsRrn = new Rnr(FACILITY, PROGRAM, secondLastPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, secondLastPeriod)).thenReturn(secondLastPeriodsRrn);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    final Rnr actual = requisitionService.get(FACILITY, PROGRAM, PERIOD);
    assertThat(actual, is(spyRnr));
    verify(spyRnr).fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRrn, secondLastPeriodsRrn);
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsAtLeastOneExistingRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20, date2);
    ProcessingPeriod processingPeriod3 = createProcessingPeriod(30, date3);
    ProcessingPeriod processingPeriod4 = createProcessingPeriod(40, date4);

    createRequisition(processingPeriod1.getId(), AUTHORIZED);
    Rnr rnr2 = createRequisition(processingPeriod2.getId(), APPROVED);
    createRequisition(processingPeriod3.getId(), INITIATED);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(rnr2);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(), processingPeriod2.getId())).
        thenReturn(Arrays.asList(processingPeriod3, processingPeriod4));

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
    assertThat(periods.get(0), is(processingPeriod3));
    assertThat(periods.get(1), is(processingPeriod4));
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsNoRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20, date2);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(null);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(), null)).
        thenReturn(Arrays.asList(processingPeriod1, processingPeriod2));

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
    assertThat(periods.get(0), is(processingPeriod1));
    assertThat(periods.get(1), is(processingPeriod2));
  }

  private Rnr createRequisition(int periodId, RnrStatus status) {
    return make(a(RequisitionBuilder.defaultRnr,
        with(RequisitionBuilder.periodId, periodId),
        with(RequisitionBuilder.status, status)));
  }

  private ProcessingPeriod createProcessingPeriod(int id, DateTime startDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
        with(ProcessingPeriodBuilder.startDate, startDate.toDate())));
    processingPeriod.setId(id);
    return processingPeriod;
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);

    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(new ArrayList<RnrColumn>());
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_TEMPLATE_NOT_INITIATED_ERROR);

    Rnr rnr = requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(facilityApprovedProductService, never()).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), HIV);
    verify(requisitionRepository, never()).insert(rnr);
  }

  @Test
  public void shouldNotInitRequisitionIfPeriodDoesNotAllowInitiation() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    ProcessingPeriod validPeriod = new ProcessingPeriod(1);
    setupForInitRnr(date, requisition, validPeriod);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_PREVIOUS_NOT_FILLED_ERROR);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(programService).getProgramStartDate(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId());
    verify(processingScheduleService).getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, validPeriod.getId());
  }

  private void setupForInitRnr(Date date, Rnr requisition, ProcessingPeriod validPeriod) {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);
    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(getRnrColumns());
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date);
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(requisition);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, PERIOD.getId())).
        thenReturn(Arrays.asList(validPeriod));
  }

  @Test
  public void shouldUseOnlyUserEditableDataFromUserSubmittedRnr() throws Exception {
    List<RnrColumn> rnrColumns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, "beginningBalance"), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, "stockInHand"), with(visible, false))));
    }};

    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    Mockito.when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(PROGRAM.getId())).thenReturn(rnrColumns);
    doNothing().when(savedRnr).copyApproverEditableFields(initiatedRnr);
    doNothing().when(savedRnr).prepareFor(SUBMITTED, rnrColumns);

    requisitionService.submit(initiatedRnr);

    verify(savedRnr).prepareFor(SUBMITTED, rnrColumns);
    verify(requisitionRepository).update(savedRnr);
    verify(savedRnr).copyUserEditableFields(initiatedRnr, rnrColumns);
  }

  @Test
  public void shouldReturnMessageWhileSubmittingRnrIfSupervisingNodeNotPresent() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    Mockito.when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(PROGRAM.getId())).thenReturn(rnrColumns);
    doNothing().when(savedRnr).copyUserEditableFields(initiatedRnr, rnrColumns);
    doNothing().when(savedRnr).calculate(rnrColumns);

    OpenLmisMessage message = requisitionService.submit(initiatedRnr);

    verify(savedRnr).prepareFor(SUBMITTED, rnrColumns);
    verify(requisitionRepository).update(savedRnr);
    verify(savedRnr).copyUserEditableFields(initiatedRnr, rnrColumns);
    verify(requisitionRepository).update(savedRnr);
    assertThat(message.getCode(), is("rnr.submitted.without.supervisor"));
  }

  @Test
  public void shouldSubmitValidRnrWithSubmittedDateAndSetMessage() {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    doNothing().when(savedRnr).copyUserEditableFields(initiatedRnr, rnrColumns);
    doNothing().when(savedRnr).calculate(rnrColumns);
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(new SupervisoryNode());
    when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(PROGRAM.getId())).thenReturn(rnrColumns);

    OpenLmisMessage message = requisitionService.submit(initiatedRnr);

    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(savedRnr.getSubmittedDate(), is(notNullValue()));
    assertThat(savedRnr.getStatus(), is(SUBMITTED));
    assertThat(message.getCode(), is("rnr.submitted.success"));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndTagWithSupervisoryNode() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    doNothing().when(savedRnr).copyUserEditableFields(submittedRnr, rnrColumns);
    doNothing().when(savedRnr).calculate(rnrColumns);
    when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(PROGRAM.getId())).thenReturn(rnrColumns);
    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);

    OpenLmisMessage authorize = requisitionService.authorize(submittedRnr);

    verify(rnrTemplateRepository).fetchRnrTemplateColumnsOrMasterColumns(PROGRAM.getId());
    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(savedRnr.getStatus(), is(AUTHORIZED));
    assertThat(savedRnr.getSupervisoryNodeId(), is(approverNode.getId()));
    assertThat(authorize.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY));
  }

  @Test
  public void shouldUseSavedRnrWithEditableDataFromUserSuppliedRnrToAuthorize() {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    doNothing().when(savedRnr).copyUserEditableFields(submittedRnr, rnrColumns);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);
    doNothing().when(savedRnr).calculate(rnrColumns);

    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    when(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram())).thenReturn(node);
    when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(submittedRnr.getProgram().getId())).thenReturn(rnrColumns);

    requisitionService.authorize(submittedRnr);

    verify(savedRnr).copyUserEditableFields(submittedRnr, rnrColumns);
  }

  @Test
  public void shouldAuthorizeAValidRnrAndAdviseUserIfRnrDoesNotHaveApprover() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);

    when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(savedRnr.getProgram().getId())).thenReturn(rnrColumns);
    when(supervisoryNodeService.getApproverFor(savedRnr.getFacility(), savedRnr.getProgram())).thenReturn(null);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    when(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram())).thenReturn(node);
    doNothing().when(savedRnr).copyUserEditableFields(submittedRnr, rnrColumns);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);
    doNothing().when(savedRnr).calculate(rnrColumns);

    OpenLmisMessage openLmisMessage = requisitionService.authorize(submittedRnr);

    verify(rnrTemplateRepository).fetchRnrTemplateColumnsOrMasterColumns(savedRnr.getProgram().getId());
    verify(requisitionRepository).update(savedRnr);
    assertThat(savedRnr.getStatus(), is(AUTHORIZED));
    assertThat(openLmisMessage.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldNotAuthorizeRnrIfNotSubmitted() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, AUTHORIZE_REQUISITION);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_AUTHORIZATION_ERROR);

    requisitionService.authorize(initiatedRnr);
  }

  @Test
  public void shouldSaveRnrIfUserHasAppropriatePermission() {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);

    when(rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(initiatedRnr.getProgram().getId())).thenReturn(rnrColumns);
    doNothing().when(savedRnr).copyUserEditableFields(initiatedRnr, rnrColumns);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);

    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(true);

    initiatedRnr.setModifiedBy(USER_ID);
    requisitionService.save(initiatedRnr);

    verify(savedRnr).copyEditableFields(initiatedRnr, rnrColumns);
    verify(requisitionRepository).update(savedRnr);
  }

  @Test
  public void shouldNotSaveUserDoesNotHaveAppropriatePermission() {
    Rnr savedRnr = spy(submittedRnr);
    savedRnr.setModifiedBy(USER_ID);
    savedRnr.setFacility(FACILITY);
    savedRnr.setProgram(PROGRAM);
    savedRnr.setPeriod(PERIOD);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(savedRnr);

    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.save(submittedRnr);
  }

  @Test
  public void shouldFetchAllRequisitionsForFacilitiesAndProgramSupervisedByUserForApproval() throws Exception {
    final RoleAssignment firstAssignment = new RoleAssignment(1, 1, 1, new SupervisoryNode());
    final RoleAssignment secondAssignment = new RoleAssignment(2, 2, 2, new SupervisoryNode());
    final Rnr requisition = make(a(RequisitionBuilder.defaultRnr));
    final List<Rnr> requisitionsForFirstAssignment = new ArrayList<Rnr>() {{
      add(requisition);
    }};
    final List<Rnr> requisitionsForSecondAssignment = new ArrayList<>();
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(firstAssignment);
      add(secondAssignment);
    }};
    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, USER_ID)).thenReturn(roleAssignments);
    when(requisitionRepository.getAuthorizedRequisitions(firstAssignment)).thenReturn(requisitionsForFirstAssignment);
    when(requisitionRepository.getAuthorizedRequisitions(secondAssignment)).thenReturn(requisitionsForSecondAssignment);
    Program expectedProgram = new Program();
    Facility expectedFacility = new Facility();
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(programService.getById(3)).thenReturn(expectedProgram);
    when(facilityService.getById(3)).thenReturn(expectedFacility);
    when(processingScheduleService.getPeriodById(3)).thenReturn(expectedPeriod);

    List<Rnr> requisitions = requisitionService.listForApproval(USER_ID);

    List<Rnr> expectedRequisitions = new ArrayList<Rnr>() {{
      addAll(requisitionsForFirstAssignment);
      addAll(requisitionsForSecondAssignment);
    }};

    assertThat(requisitions, is(expectedRequisitions));
    assertThat(requisition.getProgram(), is(expectedProgram));
    assertThat(requisition.getFacility(), is(expectedFacility));
    assertThat(requisition.getPeriod(), is(expectedPeriod));
    verify(requisitionRepository, times(1)).getAuthorizedRequisitions(firstAssignment);
    verify(requisitionRepository, times(1)).getAuthorizedRequisitions(secondAssignment);
  }

  @Test
  public void shouldNotApproveAnRnrIfStatusIsNotAuthorized() throws Exception {
    Rnr savedRnr = spy(submittedRnr);
    savedRnr.setFacility(FACILITY);
    savedRnr.setProgram(PROGRAM);
    savedRnr.setPeriod(PERIOD);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(savedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.approve(submittedRnr);
  }

  @Test
  public void shouldApproveAnRnrAndChangeStatusToApprovedIfThereIsNoFurtherApprovalNeeded() throws Exception {

    int supervisoryNodeId = 1;
    int supplyingFacilityId = 2;
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(supervisoryNodeId);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(supervisoryNodeId);
    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility();
    supplyingFacility.setId(supplyingFacilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, savedRnr.getProgram())).thenReturn(supplyLine);

    OpenLmisMessage message = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(savedRnr.getStatus(), is(APPROVED));
    assertThat(savedRnr.getSupervisoryNodeId(), is(nullValue()));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeeded() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);

    savedRnr.setSupervisoryNodeId(1);
    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2);
    }};
    when(supervisoryNodeService.getParent(1)).thenReturn(parentNode);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, PROGRAM)).thenReturn(new User());

    OpenLmisMessage message = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(savedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(savedRnr.getSupervisoryNodeId(), is(2));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeededAndShouldGiveMessageIfThereIsNoSupervisorAssigned() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(1);

    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2);
    }};
    when(supervisoryNodeService.getParent(1)).thenReturn(parentNode);

    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, authorizedRnr.getProgram())).thenReturn(null);
    OpenLmisMessage message = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).update(savedRnr);
    assertThat(savedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(savedRnr.getSupervisoryNodeId(), is(2));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldGetRequisitionForApprovalById() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    when(requisitionPermissionService.hasPermissionToApprove(USER_ID, savedRnr)).thenReturn(true);

    Rnr actual = requisitionService.getRnrForApprovalById(authorizedRnr.getId(), USER_ID);

    assertThat(actual, is(savedRnr));
  }

  @Test
  public void shouldThrowExceptionIfUserDoesNotHaveAccessToRequestedRequisition() throws Exception {
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    Rnr expected = new Rnr();
    expected.setFacility(FACILITY);
    expected.setProgram(PROGRAM);
    expected.setPeriod(PERIOD);
    final int supervisoryNodeId = 1;
    expected.setSupervisoryNodeId(supervisoryNodeId);
    final int rnrId = 1;
    when(requisitionRepository.getById(rnrId)).thenReturn(expected);

    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
    }};

    final int userId = 1;
    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);
    requisitionService.getRnrForApprovalById(rnrId, userId);
  }

  @Test
  public void shouldFillBeginningBalanceOfLineItemsFromPreviousRequisitionIfAvailableDuringInitialize() throws Exception {
    Date date = new Date();
    ProcessingPeriod period = new ProcessingPeriod(10);
    Rnr someRequisition = createRequisition(period.getId(), null);
    Rnr previousRnr = make(a(defaultRnr));
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(id, period.getId() - 1)));
    setupForInitRnr(date, someRequisition, period);

    Rnr spyRequisition = spy(someRequisition);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    ProgramProduct programProduct2 = new ProgramProduct(null, make(a(defaultProduct, with(code, "testCode"))), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct2, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);
    when(processingScheduleService.getImmediatePreviousPeriod(spyRequisition.getPeriod())).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisition(spyRequisition.getFacility(), spyRequisition.getProgram(), previousPeriod)).thenReturn(previousRnr);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), period.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), period.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalances(previousRnr, true);
  }

  @Test
  public void shouldNotFillBeginningBalanceIfPreviousRnrNotDefinedDuringInitiate() throws Exception {
    Date date = new Date();
    Rnr someRequisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, someRequisition, PERIOD);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);

    Rnr spyRequisition = spy(someRequisition);
    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    int previousPeriodId = PERIOD.getId() - 1;
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(id, previousPeriodId)));
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(processingScheduleService.getImmediatePreviousPeriod(PERIOD)).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, previousPeriod)).thenReturn(null);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalances(null, true);
  }

  @Test
  public void shouldFillBeginningBalanceFromPreviousRequisitionEvenIfStockInHandIsNotDisplayed() throws Exception {
    Date date = new Date();
    ProcessingPeriod period = new ProcessingPeriod(10);
    Rnr someRequisition = createRequisition(period.getId(), null);
    Rnr previousRnr = make(a(defaultRnr));
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(id, period.getId() - 1)));
    setupForInitRnr(date, someRequisition, period);
    when(rnrTemplateRepository.fetchColumnsForRequisition(PROGRAM.getId())).thenReturn(getRnrColumns());

    Rnr spyRequisition = spy(someRequisition);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    ProgramProduct programProduct2 = new ProgramProduct(null, make(a(defaultProduct, with(code, "testCode"))), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct2, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);
    when(processingScheduleService.getImmediatePreviousPeriod(spyRequisition.getPeriod())).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisition(spyRequisition.getFacility(), spyRequisition.getProgram(), previousPeriod)).thenReturn(previousRnr);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), period.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), period.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalances(previousRnr, true);
  }

  @Test
  public void shouldDoCalculatePacksToShipAndCostOnApprove() throws Exception {
    Rnr spyRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);

    doNothing().when(spyRnr).calculateForApproval();

    requisitionService.approve(spyRnr);

    verify(spyRnr).calculateForApproval();
  }

  @Test
  public void shouldGetRequisitionsForViewForGivenFacilityProgramsAndPeriodRange() throws Exception {
    final Rnr requisition = make(a(RequisitionBuilder.defaultRnr));
    final List<Rnr> expected = new ArrayList<Rnr>() {{
      add(requisition);
    }};
    Program expectedProgram = new Program();
    Facility expectedFacility = new Facility();
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(programService.getById(3)).thenReturn(expectedProgram);
    when(facilityService.getById(3)).thenReturn(expectedFacility);
    when(processingScheduleService.getPeriodById(3)).thenReturn(expectedPeriod);

    Facility facility = new Facility(1);
    Program program = new Program(2);

    Date dateRangeStart = DateTime.parse("2013-02-01").toDate();
    Date dateRangeEnd = DateTime.parse("2013-02-14").toDate();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facility.getId(), program.getId(), dateRangeStart, dateRangeEnd);
    RequisitionSearchStrategy searchStrategy = mock(RequisitionSearchStrategy.class);
    when(requisitionFactory.getSearchStrategy(criteria)).thenReturn(searchStrategy);
    when(searchStrategy.search(criteria)).thenReturn(expected);

    List<Rnr> actual = requisitionService.get(criteria);

    assertThat(actual, is(expected));
    verify(requisitionFactory).getSearchStrategy(criteria);
    verify(programService).getById(3);
    verify(facilityService).getById(3);
    verify(processingScheduleService).getPeriodById(3);
  }

  @Test
  public void shouldCreateOrderBatchesBasedOnSupplyingFacilitiesOfRequisitions() throws Exception {
    Facility facility1 = new Facility(1);
    Facility facility2 = new Facility(2);

    Rnr rnr1 = new Rnr();
    rnr1.setId(10);
    rnr1.setSupplyingFacility(facility1);

    Rnr rnr2 = new Rnr();
    rnr2.setId(20);
    rnr2.setSupplyingFacility(facility2);

    Rnr rnr3 = new Rnr();
    rnr3.setId(30);
    rnr3.setSupplyingFacility(facility1);

    List<Rnr> rnrList = Arrays.asList(rnr1, rnr2, rnr3);

    OrderBatch orderBatch1 = new OrderBatch(facility1, 1);
    OrderBatch orderBatch2 = new OrderBatch(facility2, 1);

    when(requisitionRepository.getById(rnr1.getId())).thenReturn(rnr1);
    when(requisitionRepository.getById(rnr2.getId())).thenReturn(rnr2);
    when(requisitionRepository.getById(rnr3.getId())).thenReturn(rnr3);

    requisitionService.releaseRequisitionsAsOrder(rnrList, 1);

    verify(requisitionRepository).createOrderBatch(orderBatch1);
    verify(requisitionRepository).createOrderBatch(orderBatch2);

    ArgumentCaptor<Rnr> requisitionArgumentCaptor = ArgumentCaptor.forClass(Rnr.class);
    verify(requisitionRepository, times(3)).update(requisitionArgumentCaptor.capture());
    verify(requisitionRepository, times(3)).logStatusChange(requisitionArgumentCaptor.capture());

    List<Rnr> orderList = requisitionArgumentCaptor.getAllValues();
    assertThat(orderList.get(0), is(rnr1));
    assertThat(orderList.get(0).getOrderBatch(), is(orderBatch1));
    assertThat(orderList.get(1), is(rnr2));
    assertThat(orderList.get(1).getOrderBatch(), is(orderBatch2));
    assertThat(orderList.get(2), is(rnr3));
    assertThat(orderList.get(2).getOrderBatch(), is(orderBatch1));
  }

  @Test
  public void shouldGetFullRequisitionById() {
    Integer requisitionId = 1;
    Rnr requisition = spy(new Rnr());
    requisition.setFacility(FACILITY);
    requisition.setProgram(PROGRAM);
    requisition.setPeriod(PERIOD);
    requisition.setId(requisitionId);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(requisitionId)).thenReturn(requisition);

    requisitionService.getFullRequisitionById(requisitionId);

    verify(requisitionRepository).getById(requisitionId);
    verify(facilityService).getById(FACILITY.getId());
    verify(programService).getById(PROGRAM.getId());
    verify(processingScheduleService).getPeriodById(PERIOD.getId());
  }

  @Test
  public void shouldCheckForPermissionBeforeInitiatingRnr() throws Exception {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);
  }

  @Test
  public void shouldCheckForPermissionBeforeSubmittingRnr() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);

    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, CREATE_REQUISITION)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.submit(initiatedRnr);
  }

  @Test
  public void shouldCheckForPermissionBeforeAuthorizingRnr() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, CREATE_REQUISITION);

    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, AUTHORIZE_REQUISITION)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.authorize(submittedRnr);
  }

  @Test
  public void shouldCheckForPermissionBeforeApprovingRnr() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, CREATE_REQUISITION);

    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, APPROVE_REQUISITION)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.approve(authorizedRnr);
  }

  @Test
  public void shouldGetAllFilledOrders() throws Exception {
    List<Rnr> rnrs = new ArrayList<>();
    rnrs.add(getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION));
    when(requisitionRepository.getByStatus(ORDERED)).thenReturn(rnrs);
    when(facilityService.getById(submittedRnr.getSupplyingFacility().getId())).thenReturn(FACILITY);

    List<Rnr> actualRnrs = requisitionService.getOrders();

    assertThat(actualRnrs, is(rnrs));
    assertThat(actualRnrs.get(0).getFacility(), is(FACILITY));
    assertThat(actualRnrs.get(0).getProgram(), is(PROGRAM));
    assertThat(actualRnrs.get(0).getPeriod().getName(), is(PERIOD.getName()));
    assertThat(actualRnrs.get(0).getPeriod().getStartDate(), is(PERIOD.getStartDate()));
    assertThat(actualRnrs.get(0).getPeriod().getEndDate(), is(PERIOD.getEndDate()));
    assertThat(actualRnrs.get(0).getSupplyingFacility(), is(FACILITY));
    verify(requisitionRepository).getByStatus(ORDERED);
  }

  @Test
  public void shouldGetCategoryCount(){
    Rnr requisition = new Rnr();
    boolean fullSupply = true;
    when(requisitionRepository.getCategoryCount(requisition , fullSupply)).thenReturn(10);
    Integer categoryCount = requisitionService.getCategoryCount(requisition, fullSupply);
    assertThat(categoryCount, is(10));
    verify(requisitionRepository).getCategoryCount(requisition, fullSupply);
  }

  @Test
  public void shouldInsertComment() throws Exception {
    Comment comment = new Comment();
    requisitionService.insertComment(comment);
    verify(requisitionRepository).insertComment(comment);
  }

  @Test
  public void shouldGetAllCommentsForARnr() throws Exception {
    requisitionService.getCommentsByRnrId(1);
    verify(requisitionRepository).getCommentsByRnrID(1);
  }

  private Rnr getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(Rnr rnr, Right right) {
    Rnr savedRnr = spy(rnr);
    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, right)).thenReturn(true);
    when(programService.getById(savedRnr.getProgram().getId())).thenReturn(PROGRAM);
    when(facilityService.getById(savedRnr.getProgram().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(savedRnr.getProgram().getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(rnr.getId())).thenReturn(savedRnr);
    return savedRnr;
  }
}
