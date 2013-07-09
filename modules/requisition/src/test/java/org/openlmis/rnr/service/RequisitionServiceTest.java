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
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.factory.RequisitionSearchStrategyFactory;
import org.openlmis.rnr.search.strategy.RequisitionSearchStrategy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.numberOfMonths;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RegimenLineItem.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.rnr.service.RequisitionService.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionService.class)
public class RequisitionServiceTest {

  private static final Long HIV = 1L;
  private static final Facility FACILITY = new Facility(1L);
  private static final Program PROGRAM = new Program(3L);
  private static final ProcessingPeriod PERIOD = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, 10L), with(numberOfMonths, 1)));
  private static final Long USER_ID = 1L;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private FacilityApprovedProductService facilityApprovedProductService;
  @Mock
  private RequisitionRepository requisitionRepository;
  @Mock
  private RnrTemplateService rnrTemplateService;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RoleAssignmentService roleAssignmentService;
  @Mock
  private ProgramService programService;
  @Mock
  private ProcessingScheduleService processingScheduleService;
  @Mock
  private FacilityService facilityService;
  @Mock
  private SupplyLineService supplyLineService;
  @Mock
  private RegimenService regimenService;
  @Mock
  private RegimenColumnService regimenColumnService;

  @Mock
  RequisitionEventService requisitionEventService;

  @Mock
  private RequisitionPermissionService requisitionPermissionService;

  @Mock
  private UserService userService;

  @InjectMocks
  private RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;

  @InjectMocks
  private RequisitionService requisitionService;


  private Rnr submittedRnr;
  private Rnr initiatedRnr;
  private Rnr authorizedRnr;
  private Rnr inApprovalRnr;
  private ArrayList<RnrColumn> rnrColumns;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  @Before
  public void setup() {
    requisitionService.setRequisitionSearchStrategyFactory(requisitionSearchStrategyFactory);
    submittedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, SUBMITTED), with(modifiedBy, USER_ID)));
    initiatedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, INITIATED), with(modifiedBy, USER_ID)));
    authorizedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, AUTHORIZED), with(modifiedBy, USER_ID)));
    inApprovalRnr = make(a(defaultRnr, with(status, IN_APPROVAL), with(modifiedBy, USER_ID)));
    rnrColumns = new ArrayList<RnrColumn>() {{
      add(new RnrColumn());
    }};
    lossesAndAdjustmentsTypes = mock(ArrayList.class);
    when(requisitionService.getLossesAndAdjustmentsTypes()).thenReturn(lossesAndAdjustmentsTypes);
  }

  @Test
  public void shouldInitRequisitionAndSetFieldValuesAccordingToTemplate() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);

    List<Regimen> regimens = new ArrayList<>();
    regimens.add(new Regimen("name", "code", 1L, true, new RegimenCategory("code", "name", 1), 1));

    List<RegimenLineItem> regimenLineItems = new ArrayList<>();
    regimenLineItems.add(new RegimenLineItem(null, regimens.get(0)));
    requisition.setRegimenLineItems(regimenLineItems);
    Rnr spyRequisition = spy(requisition);
    Mockito.doNothing().when(spyRequisition).setFieldsAccordingToTemplate(any(ProgramRnrTemplate.class), any(RegimenTemplate.class));
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    List<RegimenColumn> regimenColumns = new ArrayList<>();
    regimenColumns.add(new RegimenColumn(PROGRAM.getId(), INITIATED_TREATMENT, "label", TYPE_NUMERIC, true));
    regimenColumns.add(new RegimenColumn(PROGRAM.getId(), ON_TREATMENT, "label", TYPE_NUMERIC, false));
    when(regimenColumnService.getRegimenColumnsByProgramId(PROGRAM.getId())).thenReturn(regimenColumns);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(spyRequisition);

    RequisitionService spyRequisitionService = spy(requisitionService);
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId());
    doReturn(asList(spyRequisition)).when(spyRequisitionService).get(criteria);

    Rnr rnr = spyRequisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1L);

    verify(facilityApprovedProductService).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).insert(any(Rnr.class));
    verify(requisitionRepository).logStatusChange(any(Rnr.class));
    verify(regimenColumnService).getRegimenColumnsByProgramId(PROGRAM.getId());

    assertThat(rnr, is(spyRequisition));
  }

  @Test
  public void shouldInitRequisitionAndSetBeginningBalanceToZeroIfNotVisibleAndPreviousStockInHandNotAvailable() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);

    ArrayList<RnrColumn> rnrColumns = getRnrColumns();
    rnrColumns.add(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE), with(visible, false))));
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(rnrColumns));
    when(requisitionRepository.getRequisitionWithLineItems(FACILITY, PROGRAM, PERIOD)).thenReturn(requisition);

    List<Regimen> regimens = new ArrayList<>();
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(requisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1L);

    assertThat(requisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldInitRequisitionAndNotSetBeginningBalanceToZeroIfVisibleAndPreviousStockInHandNotAvailable() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    requisition.getFullSupplyLineItems().get(0).setBeginningBalance(null);
    setupForInitRnr(date, requisition, PERIOD);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);

    ArrayList<RnrColumn> rnrColumns = getRnrColumns();
    rnrColumns.add(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE), with(visible, true))));
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(rnrColumns));
    List<Regimen> regimens = new ArrayList<>();
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(requisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1L);

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

    when(requisitionRepository.getRequisitionWithLineItems(FACILITY, PROGRAM, new ProcessingPeriod(PERIOD.getId()))).thenReturn(requisition);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId());
    Rnr actualRequisition = requisitionService.get(criteria).get(0);

    assertThat(actualRequisition, is(requisition));
    verify(requisition).fillBasicInformation(FACILITY, PROGRAM, PERIOD);
  }


  @Test
  public void shouldGetPreviousTwoRequisitionsNormalizedConsumptionsWhileGettingRequisition() throws Exception {
    final Long lastPeriodId = 2L;
    final Long secondLastPeriodsId = 3L;
    ProcessingPeriod lastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, lastPeriodId)));

    Rnr rnr = new Rnr(FACILITY, PROGRAM, PERIOD);
    final Rnr spyRnr = spy(rnr);

    when(requisitionRepository.getRequisitionWithLineItems(new Facility(FACILITY.getId()), new Program(PROGRAM.getId()), new ProcessingPeriod(PERIOD.getId()))).thenReturn(spyRnr);
    ProcessingPeriod period = new ProcessingPeriod(PERIOD.getId(), PERIOD.getStartDate(), PERIOD.getEndDate(), PERIOD.getNumberOfMonths(), PERIOD.getName());
    when(processingScheduleService.getPeriodById(10L)).thenReturn(period);

    when(processingScheduleService.getImmediatePreviousPeriod(period)).thenReturn(lastPeriod);

    ProcessingPeriod secondLastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, secondLastPeriodsId)));
    when(processingScheduleService.getImmediatePreviousPeriod(lastPeriod)).thenReturn(secondLastPeriod);

    Rnr lastPeriodsRnr = new Rnr(FACILITY, PROGRAM, lastPeriod);
    when(requisitionRepository.getRequisitionWithLineItems(FACILITY, PROGRAM, lastPeriod)).thenReturn(lastPeriodsRnr);

    Rnr secondLastPeriodsRnr = new Rnr(FACILITY, PROGRAM, secondLastPeriod);
    when(requisitionRepository.getRequisitionWithLineItems(FACILITY, PROGRAM, secondLastPeriod)).thenReturn(secondLastPeriodsRnr);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId());

    final Rnr actual = requisitionService.get(criteria).get(0);

    assertThat(actual, is(spyRnr));
    verify(spyRnr).fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsAtLeastOneExistingRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10L, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20L, date2);
    ProcessingPeriod processingPeriod3 = createProcessingPeriod(30L, date3);
    ProcessingPeriod processingPeriod4 = createProcessingPeriod(40L, date4);

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

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10L, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20L, date2);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(null);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(), null)).
      thenReturn(Arrays.asList(processingPeriod1, processingPeriod2));

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
    assertThat(periods.get(0), is(processingPeriod1));
    assertThat(periods.get(1), is(processingPeriod2));
  }

  private Rnr createRequisition(Long periodId, RnrStatus status) {
    return make(a(RequisitionBuilder.defaultRnr,
      with(RequisitionBuilder.periodId, periodId),
      with(RequisitionBuilder.status, status)));
  }

  private ProcessingPeriod createProcessingPeriod(Long id, DateTime startDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.startDate, startDate.toDate())));
    processingPeriod.setId(id);
    return processingPeriod;
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);

    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.template.not.defined");

    Rnr rnr = requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(facilityApprovedProductService, never()).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), HIV);
    verify(requisitionRepository, never()).insert(rnr);
  }

  @Test
  public void shouldNotInitRequisitionIfPeriodDoesNotAllowInitiation() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    ProcessingPeriod validPeriod = new ProcessingPeriod(1L);
    setupForInitRnr(date, requisition, validPeriod);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.previous.not.filled");

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(programService).getProgramStartDate(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId());
    verify(processingScheduleService).getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, validPeriod.getId());
  }

  private void setupForInitRnr(Date date, Rnr requisition, ProcessingPeriod validPeriod) {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(getRnrColumns()));
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date);
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(requisition);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, PERIOD.getId())).
      thenReturn(Arrays.asList(validPeriod));
  }

  private void setupForInitRnr(Rnr requisition) {
    Date date = new Date();
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(getRnrColumns()));
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date);
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(requisition);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, PERIOD.getId())).
      thenReturn(asList(PERIOD));
  }

  @Test
  public void shouldReturnMessageIfSupervisingNodeNotPresent() throws Exception {
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(null);

    OpenLmisMessage message = requisitionService.getSubmitMessageBasedOnSupervisoryNode(FACILITY, PROGRAM);

    assertThat(message.getCode(), is("msg.rnr.submitted.without.supervisor"));
  }

  @Test
  public void shouldSubmitValidRnrWithSubmittedDate() {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(new SupervisoryNode());

    Rnr submittedRnr = requisitionService.submit(initiatedRnr);

    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(submittedRnr.getSubmittedDate(), is(notNullValue()));
    assertThat(submittedRnr.getStatus(), is(SUBMITTED));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndTagWithSupervisoryNode() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);

    Rnr authorizedRnr = requisitionService.authorize(submittedRnr);

    verify(rnrTemplateService).fetchProgramTemplate(PROGRAM.getId());
    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(authorizedRnr.getStatus(), is(AUTHORIZED));
    assertThat(authorizedRnr.getSupervisoryNodeId(), is(approverNode.getId()));
  }

  @Test
  public void shouldAuthorizeAValidRnr() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);

    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    when(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram())).thenReturn(node);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);

    Rnr authorizedRnr = requisitionService.authorize(submittedRnr);

    verify(rnrTemplateService).fetchProgramTemplate(savedRnr.getProgram().getId());
    verify(requisitionRepository).update(savedRnr);
    assertThat(authorizedRnr.getStatus(), is(AUTHORIZED));
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
  public void shouldGiveSuccessMessageIfApproverExist() throws Exception {
    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());

    OpenLmisMessage message = requisitionService.getAuthorizeMessageBasedOnSupervisoryNode(FACILITY, PROGRAM);

    assertThat(message.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY));
  }

  @Test
  public void shouldGiveAuthorizedSuccessWithoutApproverMessageIfApproverDoesNotExist() throws Exception {
    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(null);

    OpenLmisMessage message = requisitionService.getAuthorizeMessageBasedOnSupervisoryNode(FACILITY, PROGRAM);

    assertThat(message.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldGiveApprovedSuccessIfParentDoesNotExist() throws Exception {

    Rnr rnr = make(a(defaultRnr));
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(null);
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldGiveApprovedSuccessIfParentExistWtihSupervisor() throws Exception {

    Rnr rnr = make(a(defaultRnr));
    SupervisoryNode parent = new SupervisoryNode();
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(parent);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram())).thenReturn(new User());
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }


  @Test
  public void shouldGiveApprovedSuccessWithoutSupervisorIfApproverDoesNotExitAtParentNode() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    SupervisoryNode parent = new SupervisoryNode();
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(parent);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram())).thenReturn(null);
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR));

  }

  @Test
  public void shouldSaveRnrIfUserHasAppropriatePermission() {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);

    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(initiatedRnr.getProgram().getId())).thenReturn(template);
    doNothing().when(savedRnr).copyCreatorEditableFields(initiatedRnr, template);
    doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);

    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(true);

    initiatedRnr.setModifiedBy(USER_ID);
    requisitionService.save(initiatedRnr);

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
    when(facilityService.getById(submittedRnr.getSupplyingFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(savedRnr);

    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.save(submittedRnr);
  }

  @Test
  public void shouldFetchAllRequisitionsForFacilitiesAndProgramSupervisedByUserForApproval() throws Exception {
    final RoleAssignment firstAssignment = new RoleAssignment(1L, 1L, 1L, new SupervisoryNode());
    final RoleAssignment secondAssignment = new RoleAssignment(2L, 2L, 2L, new SupervisoryNode());
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
    when(programService.getById(3L)).thenReturn(expectedProgram);
    when(facilityService.getById(3L)).thenReturn(expectedFacility);
    when(processingScheduleService.getPeriodById(3L)).thenReturn(expectedPeriod);

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
    when(facilityService.getById(submittedRnr.getSupplyingFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(savedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.approve(submittedRnr);
  }

  @Test
  public void shouldApproveAnRnrAndChangeStatusToApprovedIfThereIsNoFurtherApprovalNeeded() throws Exception {

    Long supervisoryNodeId = 1L;
    Long supplyingFacilityId = 2L;
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(supervisoryNodeId);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(supervisoryNodeId);
    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility();
    supplyingFacility.setId(supplyingFacilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, savedRnr.getProgram())).thenReturn(supplyLine);

    Rnr rnr = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).approve(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    assertThat(savedRnr.getStatus(), is(APPROVED));
    assertThat(savedRnr.getSupervisoryNodeId(), is(nullValue()));
    assertThat(savedRnr.getModifiedBy(), is(USER_ID));

  }

  @Test
  public void shouldValidateRnrForApproval() throws Exception {
    Rnr spyRnr = spy(authorizedRnr);
    spyRnr.setFacility(FACILITY);
    spyRnr.setProgram(PROGRAM);
    spyRnr.setPeriod(PERIOD);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(facilityService.getById(submittedRnr.getSupplyingFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(spyRnr);

    Mockito.doThrow(new DataException("some error")).when(spyRnr).validateForApproval();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("some error");

    requisitionService.approve(spyRnr);
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeeded() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);

    savedRnr.setSupervisoryNodeId(1L);
    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2L);
    }};
    when(supervisoryNodeService.getParent(1L)).thenReturn(parentNode);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, PROGRAM)).thenReturn(new User());

    Rnr rnr = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).approve(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr);
    verify(requisitionEventService).notifyForStatusChange(savedRnr);
    assertThat(savedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(savedRnr.getSupervisoryNodeId(), is(2L));
    assertThat(savedRnr.getModifiedBy(), is(USER_ID));

  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeededAndShouldGiveMessageIfThereIsNoSupervisorAssigned() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(1L);

    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2L);
    }};
    when(supervisoryNodeService.getParent(1L)).thenReturn(parentNode);

    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, authorizedRnr.getProgram())).thenReturn(null);
    Rnr rnr = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).approve(savedRnr);
    assertThat(savedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(savedRnr.getSupervisoryNodeId(), is(2L));
    assertThat(savedRnr.getModifiedBy(), is(USER_ID));
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
    final Long supervisoryNodeId = 1L;
    expected.setSupervisoryNodeId(supervisoryNodeId);
    final Long rnrId = 1L;
    when(requisitionRepository.getById(rnrId)).thenReturn(expected);

    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
    }};

    final Long userId = 1L;
    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);
    requisitionService.getRnrForApprovalById(rnrId, userId);
  }

  @Test
  public void shouldFillBeginningBalanceOfLineItemsFromPreviousRequisitionIfAvailableDuringInitialize() throws Exception {
    Date date = new Date();
    ProcessingPeriod period = new ProcessingPeriod(10L);
    Rnr someRequisition = createRequisition(period.getId(), null);
    Rnr previousRnr = make(a(defaultRnr));
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, period.getId() - 1)));
    setupForInitRnr(date, someRequisition, period);

    Rnr spyRequisition = spy(someRequisition);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));
    ProgramProduct programProduct2 = new ProgramProduct(null, make(a(defaultProduct, with(code, "testCode"))), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct2, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);
    when(processingScheduleService.getImmediatePreviousPeriod(spyRequisition.getPeriod())).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisitionWithLineItems(spyRequisition.getFacility(), spyRequisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    List<Regimen> regimens = new ArrayList<>();
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), period.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(spyRequisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), period.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalances(previousRnr, true);
  }

  @Test
  public void shouldNotFillBeginningBalanceIfPreviousRnrNotDefinedDuringInitiate() throws Exception {
    Date date = new Date();
    Rnr someRequisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, someRequisition, PERIOD);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);

    Rnr spyRequisition = spy(someRequisition);

    List<Regimen> regimens = new ArrayList<>();
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(spyRequisition);

    Long previousPeriodId = PERIOD.getId() - 1L;
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, previousPeriodId)));
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(processingScheduleService.getImmediatePreviousPeriod(PERIOD)).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisitionWithLineItems(FACILITY, PROGRAM, previousPeriod)).thenReturn(null);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalances(null, true);
  }

  @Test
  public void shouldFillBeginningBalanceFromPreviousRequisitionEvenIfStockInHandIsNotDisplayed() throws Exception {
    Date date = new Date();
    ProcessingPeriod period = new ProcessingPeriod(10L);
    Rnr someRequisition = createRequisition(period.getId(), null);
    Rnr previousRnr = make(a(defaultRnr));
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, period.getId() - 1)));
    setupForInitRnr(date, someRequisition, period);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(new ProgramRnrTemplate(getRnrColumns()));

    Rnr spyRequisition = spy(someRequisition);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30));
    ProgramProduct programProduct2 = new ProgramProduct(null, make(a(defaultProduct, with(code, "testCode"))), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct2, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);
    when(processingScheduleService.getImmediatePreviousPeriod(spyRequisition.getPeriod())).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisitionWithLineItems(spyRequisition.getFacility(), spyRequisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    List<Regimen> regimens = new ArrayList<>();
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), period.getId(), facilityTypeApprovedProducts, regimens, USER_ID).thenReturn(spyRequisition);

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
    Program expectedProgram = requisition.getProgram();
    Facility expectedFacility = requisition.getFacility();
    ProcessingPeriod expectedPeriod = requisition.getPeriod();
    when(programService.getById(3L)).thenReturn(expectedProgram);
    when(facilityService.getById(3L)).thenReturn(expectedFacility);
    when(processingScheduleService.getPeriodById(3L)).thenReturn(expectedPeriod);

    Facility facility = new Facility(1L);
    Program program = new Program(2L);

    Date dateRangeStart = DateTime.parse("2013-02-01").toDate();
    Date dateRangeEnd = DateTime.parse("2013-02-14").toDate();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facility.getId(), program.getId(), dateRangeStart, dateRangeEnd);
    RequisitionSearchStrategy searchStrategy = mock(RequisitionSearchStrategy.class);
    RequisitionSearchStrategyFactory spyFactory = spy(requisitionSearchStrategyFactory);
    requisitionService.setRequisitionSearchStrategyFactory(spyFactory);
    when(spyFactory.getSearchStrategy(criteria)).thenReturn(searchStrategy);
    when(searchStrategy.search()).thenReturn(expected);

    List<Rnr> actual = requisitionService.get(criteria);

    assertThat(actual, is(expected));
    verify(spyFactory).getSearchStrategy(criteria);
    verify(programService).getById(3L);
    verify(facilityService).getById(3L);
    verify(processingScheduleService).getPeriodById(3L);
  }


  @Test
  public void shouldGetFullRequisitionById() {
    Long requisitionId = 1L;
    Rnr requisition = spy(new Rnr());
    requisition.setFacility(FACILITY);
    requisition.setProgram(PROGRAM);
    requisition.setPeriod(PERIOD);
    requisition.setId(requisitionId);
    requisition.setSupplyingFacility(FACILITY);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(requisitionId)).thenReturn(requisition);

    requisitionService.getFullRequisitionById(requisitionId);

    verify(requisitionRepository).getById(requisitionId);
    verify(facilityService, times(2)).getById(FACILITY.getId());
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
  public void shouldGetCategoryCount() {
    Rnr requisition = new Rnr();
    boolean fullSupply = true;
    when(requisitionRepository.getCategoryCount(requisition, fullSupply)).thenReturn(10);
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
  public void shouldNotifyStatusChangeEvent() throws Exception {
    Rnr requisition = createRequisition(PERIOD.getId(), INITIATED);
    setupForInitRnr(requisition);
    whenNew(Rnr.class).withAnyArguments().thenReturn(requisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1L);

    verify(requisitionEventService).notifyForStatusChange(requisition);
  }

  @Test
  public void shouldNotifyStatusChangeOnAuthorize() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);

    requisitionService.authorize(submittedRnr);

    verify(requisitionEventService).notifyForStatusChange(savedRnr);
  }

  @Test
  public void shouldSetDefaultApprovedQuantityOnAuthorization() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);
    doNothing().when(savedRnr).setDefaultApprovedQuantity();

    requisitionService.authorize(submittedRnr);

    verify(savedRnr).setDefaultApprovedQuantity();
  }

  @Test
  public void shouldNotifyStatusChangeOnSubmit() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(savedRnr).calculate(template, lossesAndAdjustmentsTypes);
    when(rnrTemplateService.fetchAllRnRColumns(PROGRAM.getId())).thenReturn(rnrColumns);

    requisitionService.submit(initiatedRnr);

    verify(requisitionEventService).notifyForStatusChange(savedRnr);
  }


  @Test
  public void shouldGetAllCommentsForARnrWithUsername() throws Exception {
    User user = make(a(UserBuilder.defaultUser));
    ArrayList<Comment> comments = new ArrayList<>();
    Comment comment = new Comment();
    User author = new User();
    author.setId(USER_ID);
    comment.setAuthor(author);
    comments.add(comment);
    when(requisitionRepository.getCommentsByRnrID(1L)).thenReturn(comments);
    User spyUser = spy(user);
    User userReturned = new User();
    userReturned.setId(1L);
    userReturned.setUserName(user.getUserName());
    when(spyUser.basicInformation()).thenReturn(userReturned);
    when(userService.getById(USER_ID)).thenReturn(spyUser);

    List<Comment> returnedComments = requisitionService.getCommentsByRnrId(1L);

    verify(requisitionRepository).getCommentsByRnrID(1L);
    User commentUser = comments.get(0).getAuthor();
    verify(spyUser).basicInformation();
    assertThat(commentUser.getUserName(), is(user.getUserName()));
    assertThat(comments, is(returnedComments));
  }

  @Test
  public void shouldReleaseRequisitionAsOrder() throws Exception {
    when(requisitionPermissionService.hasPermission(USER_ID, CONVERT_TO_ORDER)).thenReturn(true);
    final Rnr rnr = spy(authorizedRnr);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(rnr);
    List<Rnr> rnrList = new ArrayList<Rnr>() {{
      add(rnr);
    }};

    requisitionService.releaseRequisitionsAsOrder(rnrList, USER_ID);

    verify(rnr).convertToOrder(USER_ID);
  }

  @Test
  public void shouldNotifyStatusChangeToReleased() throws Exception {
    when(requisitionPermissionService.hasPermission(USER_ID, CONVERT_TO_ORDER)).thenReturn(true);
    final Rnr rnr = spy(authorizedRnr);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(rnr);
    List<Rnr> rnrList = new ArrayList<Rnr>() {{
      add(rnr);
    }};

    requisitionService.releaseRequisitionsAsOrder(rnrList, USER_ID);

    verify(requisitionEventService).notifyForStatusChange(rnr);
  }


  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreCreatorEditableBasedOnRnrStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    doNothing().when(savedRequisition).copyCreatorEditableFields(initiatedRnr, template);
    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);

    requisitionService.save(initiatedRnr);

    verify(savedRequisition).copyCreatorEditableFields(initiatedRnr, template);
    verify(requisitionRepository).update(savedRequisition);
  }

  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreApproverEditableBasedOnRnrStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);
    doNothing().when(savedRequisition).copyApproverEditableFields(authorizedRnr, template);

    requisitionService.save(authorizedRnr);

    verify(savedRequisition).copyApproverEditableFields(authorizedRnr, template);
    verify(requisitionRepository).update(savedRequisition);
  }

  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreApproverEditableBasedInApprovalStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(inApprovalRnr, APPROVE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);
    doNothing().when(savedRequisition).copyApproverEditableFields(inApprovalRnr, template);
    requisitionService.save(inApprovalRnr);

    verify(savedRequisition).copyApproverEditableFields(inApprovalRnr, template);
    verify(requisitionRepository).update(savedRequisition);
  }

  private Rnr getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(Rnr rnr, Right right) {
    Rnr savedRnr = spy(rnr);
    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(true);
    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, right)).thenReturn(true);
    when(programService.getById(savedRnr.getProgram().getId())).thenReturn(PROGRAM);
    when(facilityService.getById(savedRnr.getFacility().getId())).thenReturn(FACILITY);
    when(facilityService.getById(savedRnr.getSupplyingFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(savedRnr.getProgram().getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(rnr.getId())).thenReturn(savedRnr);
    return savedRnr;
  }
}

