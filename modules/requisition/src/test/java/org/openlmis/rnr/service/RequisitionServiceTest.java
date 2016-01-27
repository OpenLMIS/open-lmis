
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.repository.mapper.DbMapper;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.factory.RequisitionSearchStrategyFactory;
import org.openlmis.rnr.search.strategy.RequisitionSearchStrategy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.numberOfMonths;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.rnr.service.RequisitionService.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RequisitionService.class)
public class RequisitionServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private Facility FACILITY = new Facility(1L);
  private Program PROGRAM = new Program(3L);
  private ProcessingPeriod PERIOD = make(
    a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, 10L), with(numberOfMonths, 1)));
  private Long USER_ID = 1L;
  private Rnr submittedRnr;
  private Rnr initiatedRnr;
  private Rnr authorizedRnr;
  private Rnr inApprovalRnr;
  private ArrayList<RnrColumn> rnrColumns;
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
  private RequisitionEventService requisitionEventService;
  @Mock
  private RequisitionPermissionService requisitionPermissionService;
  @Mock
  private StaticReferenceDataService staticReferenceDataService;
  @Mock
  private UserService userService;
  @Mock
  private ProgramProductService programProductService;
  @Mock
  private MessageService messageService;
  @Mock
  private CalculationService calculationService;
  @Mock
  private ConfigurationSettingService configurationSettingsService;
  @Mock
  private DbMapper dbMapper;
  @Mock
  private BudgetLineItemService budgetLineItemService;
  @Mock
  private StatusChangeEventService statusChangeEventService;

  @InjectMocks
  private RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;
  @InjectMocks
  private RequisitionService requisitionService;

  @Before
  public void setup() {
    requisitionService.setRequisitionSearchStrategyFactory(requisitionSearchStrategyFactory);
    submittedRnr = make(a(RequisitionBuilder.defaultRequisition, with(status, SUBMITTED), with(modifiedBy, USER_ID)));
    initiatedRnr = make(a(RequisitionBuilder.defaultRequisition, with(status, INITIATED), with(modifiedBy, USER_ID)));
    authorizedRnr = make(a(RequisitionBuilder.defaultRequisition, with(status, AUTHORIZED), with(modifiedBy, USER_ID)));
    inApprovalRnr = make(a(defaultRequisition, with(status, IN_APPROVAL), with(modifiedBy, USER_ID)));
    when(configurationSettingsService.getBoolValue("RNR_COPY_SKIPPED_FROM_PREVIOUS_RNR")).thenReturn(false);
    rnrColumns = new ArrayList<RnrColumn>() {{
      add(new RnrColumn());
    }};
  }

  @Test
  public void shouldSetFieldValuesAccordingToTemplateOnInitiate() throws Exception {
    PROGRAM.setBudgetingApplies(true);
    PROGRAM.setPush(false);
    PROGRAM.setIsEquipmentConfigured(false);
    PROGRAM.setUsePriceSchedule(false);
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    requisition.setProgram(PROGRAM);
    setupForInitRnr();

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityTypeApprovedProducts.add(new FacilityTypeApprovedProduct("warehouse", programProduct, 30.56));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(),
      PROGRAM.getId())).thenReturn(facilityTypeApprovedProducts);

    List<Regimen> regimens = new ArrayList<>();
    regimens.add(new Regimen("name", "code", 1L, true, new RegimenCategory("code", "name", 1), 1));

    List<RegimenLineItem> regimenLineItems = new ArrayList<>();
    regimenLineItems.add(new RegimenLineItem(null, null, 1L, 1L));
    requisition.setRegimenLineItems(regimenLineItems);

    requisition.setStatus(INITIATED);
    Rnr spyRequisition = spy(requisition);

    Mockito.doNothing().when(spyRequisition).setFieldsAccordingToTemplateFrom(any(Rnr.class),
      any(ProgramRnrTemplate.class), any(RegimenTemplate.class));
    when(regimenService.getByProgram(PROGRAM.getId())).thenReturn(regimens);

    when(regimenColumnService.getRegimenTemplateByProgramId(PROGRAM.getId())).thenReturn(new RegimenTemplate());

    whenNew(Rnr.class).withArguments(FACILITY, PROGRAM, PERIOD, false, facilityTypeApprovedProducts, regimens,
      USER_ID).thenReturn(spyRequisition);

    RequisitionService spyRequisitionService = spy(requisitionService);

    when(requisitionRepository.getById(requisition.getId())).thenReturn(spyRequisition);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    doReturn(PERIOD).when(spyRequisitionService).findPeriod(FACILITY, PROGRAM, false);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(budgetLineItemService.get(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId())).thenReturn(new BudgetLineItem());

    Rnr rnr = spyRequisitionService.initiate(FACILITY, PROGRAM, 1L, false, null);

    verify(facilityApprovedProductService).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(),
      PROGRAM.getId());
    verify(processingScheduleService).getPeriodById(PERIOD.getId());
    verify(requisitionRepository).insert(any(Rnr.class));
    verify(requisitionRepository).logStatusChange(any(Rnr.class), anyString());
    verify(regimenColumnService).getRegimenTemplateByProgramId(PROGRAM.getId());

    assertThat(rnr, is(spyRequisition));
    assertThat(rnr.getPeriod().getId(), is(PERIOD.getId()));
    assertThat(rnr.getPeriod().getStartDate(), is(PERIOD.getStartDate()));
    assertThat(rnr.getPeriod().getEndDate(), is(PERIOD.getEndDate()));
  }

  @Test
  public void shouldSetFieldsInRequisitionOnInitiate() throws Exception {
    List<FacilityTypeApprovedProduct> facilityApprovedProducts = mock(List.class);
    RegimenTemplate regimenTemplate = mock(RegimenTemplate.class);
    List<Regimen> regimens = mock(List.class);

    Rnr previousRnr = new Rnr();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(3L);

    RequisitionService spyRequisitionService = spy(requisitionService);

    Program requisitionProgram = new Program(1234L);
    requisitionProgram.setBudgetingApplies(true);
    requisitionProgram.setPush(false);
    requisitionProgram.setIsEquipmentConfigured(false);
    requisitionProgram.setUsePriceSchedule(false);

    when(
      requisitionPermissionService.hasPermission(USER_ID, FACILITY, requisitionProgram, CREATE_REQUISITION)).thenReturn(
      true);
    doReturn(PERIOD).when(spyRequisitionService).findPeriod(FACILITY, requisitionProgram, false);
    when(regimenService.getByProgram(requisitionProgram.getId())).thenReturn(regimens);
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(),
      requisitionProgram.getId())).thenReturn(facilityApprovedProducts);

    when(regimenColumnService.getRegimenTemplateByProgramId(requisitionProgram.getId())).thenReturn(regimenTemplate);
    ProgramRnrTemplate rnrTemplate = new ProgramRnrTemplate(getRnrColumns());
    when(rnrTemplateService.fetchProgramTemplateForRequisition(requisitionProgram.getId())).thenReturn(rnrTemplate);

    when(requisitionRepository.getRegularRequisitionWithLineItems(FACILITY, requisitionProgram,
      previousPeriod)).thenReturn(previousRnr);
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    BigDecimal allocatedBudget = new BigDecimal(45.67);
    budgetLineItem.setAllocatedBudget(allocatedBudget);
    when(budgetLineItemService.get(FACILITY.getId(), requisitionProgram.getId(), PERIOD.getId())).thenReturn(
      budgetLineItem);
    when(programService.getById(requisitionProgram.getId())).thenReturn(requisitionProgram);
    when(budgetLineItemService.get(FACILITY.getId(), requisitionProgram.getId(), PERIOD.getId())).thenReturn(
      budgetLineItem);
    Rnr requisition = new Rnr();
    requisition.setFacility(FACILITY);
    requisition.setProgram(requisitionProgram);
    requisition.setPeriod(PERIOD);
    requisition.setStatus(INITIATED);
    whenNew(Rnr.class).withArguments(FACILITY, requisitionProgram, PERIOD, false, facilityApprovedProducts, regimens,
      USER_ID).thenReturn(requisition);

    spyRequisitionService.initiate(FACILITY, requisitionProgram, USER_ID, false, null);

    verify(calculationService).fillFieldsForInitiatedRequisition(requisition, rnrTemplate, regimenTemplate);
    assertThat(requisition.getAllocatedBudget(), is(allocatedBudget));
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsAtLeastOneExistingRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);

    PROGRAM.setPush(false);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10L, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20L, date2);
    ProcessingPeriod processingPeriod3 = createProcessingPeriod(30L, date3);
    ProcessingPeriod processingPeriod4 = createProcessingPeriod(40L, date4);

    createRequisition(processingPeriod1.getId(), AUTHORIZED);
    Rnr rnr2 = createRequisition(processingPeriod2.getId(), APPROVED);
    createRequisition(processingPeriod3.getId(), INITIATED);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRegularRequisitionToEnterThePostSubmitFlow(FACILITY.getId(),
      PROGRAM.getId())).thenReturn(rnr2);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(),
      processingPeriod2.getId())).
      thenReturn(Arrays.asList(processingPeriod3, processingPeriod4));

    when(processingScheduleService.getOpenPeriods(FACILITY.getId(), PROGRAM.getId(), processingPeriod2.getId())).thenReturn(null);

    List<ProcessingPeriod> periods =
      requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
//    assertThat(periods.get(1), is(processingPeriod3));
//    assertThat(periods.get(2), is(processingPeriod4));
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsNoRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);

    PROGRAM.setPush(false);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10L, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20L, date2);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRegularRequisitionToEnterThePostSubmitFlow(FACILITY.getId(),
      PROGRAM.getId())).thenReturn(null);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(),
      null)).
      thenReturn(Arrays.asList(processingPeriod1, processingPeriod2));
    when( processingScheduleService.getOpenPeriods(FACILITY.getId(), PROGRAM.getId(), processingPeriod2.getId()) ).thenReturn( null );

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(),
      PROGRAM.getId());

    assertThat(periods.size(), is(2));
//    assertThat(periods.get(0), is(processingPeriod1));
//    assertThat(periods.get(1), is(processingPeriod2));
  }

  @Test
  public void shouldThrowExceptionIfLastPostSubmitRequisitionIsOfCurrentPeriod() throws Exception {
    DateTime currentDate = new DateTime();

    ProcessingPeriod currentPeriod = createProcessingPeriod(10L, currentDate);

    Rnr currentRnr = createRequisition(currentPeriod.getId(), AUTHORIZED);

//    expectedException.expect(DataException.class);
//    expectedException.expectMessage("error.current.rnr.already.post.submit");

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(currentDate.toDate());
    when(requisitionRepository.getLastRegularRequisitionToEnterThePostSubmitFlow(FACILITY.getId(),
      PROGRAM.getId())).thenReturn(currentRnr);
    when(
      processingScheduleService.getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), currentDate.toDate())).thenReturn(
      currentPeriod);

    requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    verify(processingScheduleService, never()).getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(),
      currentDate.toDate(), null);
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);

    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(
      new ProgramRnrTemplate(new ArrayList<RnrColumn>()));
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.template.not.defined");

    Rnr rnr = requisitionService.initiate(FACILITY, PROGRAM, USER_ID, false, null);

    Long HIV = 1L;
    verify(facilityApprovedProductService, never()).getFullSupplyFacilityApprovedProductByFacilityAndProgram(
      FACILITY.getId(), HIV);
    verify(requisitionRepository, never()).insert(rnr);
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
    doNothing().when(calculationService).perform(savedRnr, template);
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(new SupervisoryNode());

    Rnr submittedRnr = requisitionService.submit(initiatedRnr);

    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr, null);
    assertThat(submittedRnr.getStatus(), is(SUBMITTED));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndTagWithSupervisoryNode() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(calculationService).perform(savedRnr, template);

    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);

    Rnr authorizedRnr = requisitionService.authorize(submittedRnr);

    verify(rnrTemplateService).fetchProgramTemplate(PROGRAM.getId());
    verify(requisitionRepository).update(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr, null);
    assertThat(authorizedRnr.getStatus(), is(AUTHORIZED));
    assertThat(authorizedRnr.getSupervisoryNodeId(), is(approverNode.getId()));
  }

  @Test
  public void shouldAuthorizeAValidRnr() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);

    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(calculationService).perform(savedRnr, template);
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

    Rnr rnr = make(a(defaultRequisition));
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(null);
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldGiveApprovedSuccessIfParentExistWithSupervisor() throws Exception {

    Rnr rnr = make(a(defaultRequisition));
    SupervisoryNode parent = new SupervisoryNode();
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(parent);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram())).thenReturn(
      new User());
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldGiveApprovedSuccessWithoutSupervisorIfApproverDoesNotExitAtParentNode() throws Exception {
    Rnr rnr = make(a(defaultRequisition));
    SupervisoryNode parent = new SupervisoryNode();
    when(supervisoryNodeService.getParent(rnr.getSupervisoryNodeId())).thenReturn(parent);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram())).thenReturn(
      null);
    OpenLmisMessage message = requisitionService.getApproveMessageBasedOnParentNode(rnr);

    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldSaveRnrIfUserHasAppropriatePermission() {
    Rnr savedRnr = spy(getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION));

    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    RegimenTemplate regimenTemplate = new RegimenTemplate(savedRnr.getProgram().getId(),
      new ArrayList<RegimenColumn>());
    Mockito.when(requisitionRepository.getById(savedRnr.getId())).thenReturn(savedRnr);
    when(rnrTemplateService.fetchProgramTemplate(initiatedRnr.getProgram().getId())).thenReturn(template);
    Mockito.when(regimenColumnService.getRegimenTemplateByProgramId(initiatedRnr.getProgram().getId())).thenReturn(
      regimenTemplate);
    List<ProgramProduct> programProducts = new ArrayList<>();
    Mockito.doNothing().when(savedRnr).copyCreatorEditableFields(initiatedRnr, template, regimenTemplate,
      programProducts);
    Mockito.doNothing().when(savedRnr).fillBasicInformation(FACILITY, PROGRAM, PERIOD);
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
    final Rnr requisition = make(a(RequisitionBuilder.defaultRequisition));
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

    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, APPROVE_REQUISITION)).thenReturn(true);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(savedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(APPROVAL_NOT_ALLOWED);

    requisitionService.approve(submittedRnr, null);
  }

  @Test
  public void shouldApproveAnRnrAndChangeStatusToApprovedIfThereIsNoFurtherApprovalNeeded() throws Exception {
    Long supervisoryNodeId = 1L;
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(supervisoryNodeId);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(supervisoryNodeId);
    SupplyLine supplyLine = mock(SupplyLine.class);
    savedRnr.setStatus(IN_APPROVAL);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(supplyLine);
    Facility supplyingDepot = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(supplyingDepot);
    SupervisoryNode baseSupervisoryNode = new SupervisoryNode();
    baseSupervisoryNode.setId(1L);
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(baseSupervisoryNode);
    SupplyLine supplyLine1 = mock(SupplyLine.class);
    Facility supplyingFacility = new Facility();
    supplyingFacility.setId(2L);
    supplyLine1.setSupplyingFacility(supplyingFacility);
    when(supplyLineService.getSupplyLineBy(baseSupervisoryNode, savedRnr.getProgram())).thenReturn(supplyLine);
    when(userService.getUsersWithRightOnWarehouse(supplyingFacility.getId(), CONVERT_TO_ORDER)).thenReturn(EMPTY_LIST);

    requisitionService.approve(authorizedRnr, null);

    verify(requisitionRepository).approve(savedRnr);
    verify(requisitionRepository).logStatusChange(savedRnr, null);
    assertThat(savedRnr.getStatus(), is(APPROVED));
    assertThat(savedRnr.getSupervisoryNodeId(), is(supervisoryNodeId));
    assertThat(savedRnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldValidateRnrForApproval() throws Exception {
    Rnr spyRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    spyRnr.setFacility(FACILITY);
    spyRnr.setProgram(PROGRAM);
    spyRnr.setPeriod(PERIOD);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(spyRnr);

    Mockito.doThrow(new DataException("some error")).when(spyRnr).validateForApproval();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("some error");

    requisitionService.approve(spyRnr, null);
  }

  @Test
  public void shouldKeepStatusInApprovalIfFurtherApprovalNeededAndNotNotifyStatusChange() {
    Rnr inApprovalRequisition =
      getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(inApprovalRnr, APPROVE_REQUISITION);

    inApprovalRequisition.setSupervisoryNodeId(1L);
    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2L);
    }};
    when(supervisoryNodeService.getParent(1L)).thenReturn(parentNode);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, PROGRAM)).
      thenReturn(new User());

    inApprovalRequisition.setSupervisoryNodeId(1l);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(1l);
    SupplyLine supplyLine = mock(SupplyLine.class);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(supplyLine);
    Facility supplyingDepot = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(supplyingDepot);

    requisitionService.approve(inApprovalRnr, null);

    verify(requisitionRepository).approve(inApprovalRequisition);
    verify(requisitionRepository).logStatusChange(inApprovalRequisition, null);
    verify(requisitionEventService, never()).notifyForStatusChange(inApprovalRequisition);
    assertThat(inApprovalRequisition.getStatus(), is(IN_APPROVAL));
    assertThat(inApprovalRequisition.getSupervisoryNodeId(), is(2L));
    assertThat(inApprovalRequisition.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldApproveAnAuthorizedRequisitionAndNotifyStatusChange() throws Exception {
    Rnr approvedRequisition =
      getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);

    approvedRequisition.setSupervisoryNodeId(1L);
    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2L);
    }};
    when(supervisoryNodeService.getParent(1L)).thenReturn(parentNode);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, PROGRAM)).
      thenReturn(new User());

    approvedRequisition.setSupervisoryNodeId(1l);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(1l);
    SupplyLine supplyLine = mock(SupplyLine.class);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(supplyLine);
    Facility supplyingDepot = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(supplyingDepot);

    requisitionService.approve(authorizedRnr, null);

    verify(requisitionRepository).approve(approvedRequisition);
    verify(requisitionRepository).logStatusChange(approvedRequisition, null);
    verify(requisitionEventService).notifyForStatusChange(approvedRequisition);
    assertThat(approvedRequisition.getStatus(), is(IN_APPROVAL));
    assertThat(approvedRequisition.getSupervisoryNodeId(), is(2L));
    assertThat(approvedRequisition.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeededAndShouldGiveMessageIfThereIsNoSupervisorAssigned() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);
    savedRnr.setSupervisoryNodeId(1L);

    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2L);
    }};
    when(supervisoryNodeService.getParent(1L)).thenReturn(parentNode);

    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode,
      authorizedRnr.getProgram())).thenReturn(null);

    savedRnr.setSupervisoryNodeId(1l);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(1l);
    SupplyLine supplyLine = mock(SupplyLine.class);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(supplyLine);
    Facility supplyingDepot = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(supplyingDepot);

    requisitionService.approve(authorizedRnr, null);

    verify(requisitionRepository).approve(savedRnr);
    assertThat(savedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(savedRnr.getSupervisoryNodeId(), is(2L));
    assertThat(savedRnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldDoCalculatePacksToShipAndCostOnApprove() throws Exception {
    Rnr spyRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr, APPROVE_REQUISITION);

    doNothing().when(spyRnr).calculateForApproval();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(1l);
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(supervisoryNode);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(null);
    requisitionService.approve(spyRnr, null);

    verify(spyRnr).calculateForApproval();
  }

  @Test
  public void shouldGetRequisitionsForViewForGivenFacilityProgramsAndPeriodRange() throws Exception {
    final Rnr requisition = make(a(RequisitionBuilder.defaultRequisition));

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

    String dateRangeStart = "01-02-2013";
    String dateRangeEnd = "14-02-2013";
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facility.getId()),
      with(programIdProperty, program.getId()),
      with(startDate, dateRangeStart),
      with(endDate, dateRangeEnd)));

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
    requisition.setStatus(RnrStatus.APPROVED);
    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(requisitionId)).thenReturn(requisition);

    requisition.setSupervisoryNodeId(1l);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(1l);
    SupplyLine supplyLine = mock(SupplyLine.class);

    when(supplyLineService.getSupplyLineBy(supervisoryNode, PROGRAM)).thenReturn(supplyLine);
    Facility supplyingDepot = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(supplyingDepot);

    Rnr fullRequisition = requisitionService.getFullRequisitionById(requisitionId);

    verify(requisitionRepository).getById(requisitionId);
    verify(facilityService).getById(FACILITY.getId());
    verify(programService).getById(PROGRAM.getId());
    verify(processingScheduleService).getPeriodById(PERIOD.getId());
    assertThat(fullRequisition.getSupplyingDepot(), is(supplyingDepot));
  }

  @Test
  public void shouldGetRequisitionFilledWithSupplyLine() {
    Long requisitionId = 1L;
    Rnr requisition = spy(new Rnr());
    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setId(3L);
    SupplyLine filledSupplyLine = make(a(defaultSupplyLine));
    requisition.setFacility(FACILITY);
    requisition.setProgram(PROGRAM);
    requisition.setPeriod(PERIOD);
    requisition.setId(requisitionId);
    when(supplyLineService.getById(3L)).thenReturn(filledSupplyLine);
    when(requisitionRepository.getById(requisitionId)).thenReturn(requisition);
    Mockito.doNothing().when(requisition).fillBasicInformation(any(Facility.class), any(Program.class),
      any(ProcessingPeriod.class));

    requisitionService.getFullRequisitionById(requisitionId);
  }

  @Test
  public void shouldNotFillSupplyLineIfRequisitionNotTagged() {
    Long requisitionId = 1L;
    Rnr requisition = spy(new Rnr());
    requisition.setFacility(FACILITY);
    requisition.setProgram(PROGRAM);
    requisition.setPeriod(PERIOD);
    requisition.setId(requisitionId);
    when(requisitionRepository.getById(requisitionId)).thenReturn(requisition);
    Mockito.doNothing().when(requisition).fillBasicInformation(any(Facility.class), any(Program.class),
      any(ProcessingPeriod.class));

    requisitionService.getFullRequisitionById(requisitionId);

    verify(supplyLineService, never()).getById(anyLong());
  }


  @Test
  public void shouldCheckForPermissionBeforeInitiatingRnr() throws Exception {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.initiate(FACILITY, PROGRAM, USER_ID, false, null);
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

    requisitionService.approve(authorizedRnr, null);
  }

  @Test
  public void shouldThrowErrorIfRnrCannotBeApproved() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);

    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, APPROVE_REQUISITION)).thenReturn(true);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(APPROVAL_NOT_ALLOWED);

    requisitionService.approve(authorizedRnr, null);
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
    PROGRAM.setBudgetingApplies(true);
    PROGRAM.setPush(false);
    PROGRAM.setIsEquipmentConfigured(false);
    PROGRAM.setUsePriceSchedule(false);

    Rnr requisition = spy(createRequisition(PERIOD.getId(), INITIATED));
    setupForInitRnr();
    requisition.setProgram(PROGRAM);
    RequisitionService spyRequisitionService = spy(requisitionService);
    doReturn(PERIOD).when(spyRequisitionService).findPeriod(FACILITY, PROGRAM, false);

    whenNew(Rnr.class).withAnyArguments().thenReturn(requisition);
    Mockito.doNothing().when(requisition).setFieldsAccordingToTemplateFrom(any(Rnr.class),
      any(ProgramRnrTemplate.class), any(RegimenTemplate.class));

    when(requisitionRepository.getById(requisition.getId())).thenReturn(requisition);
    when(facilityService.getById(requisition.getFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(requisition.getPeriod().getId())).thenReturn(PERIOD);
    when(programService.getById(requisition.getProgram().getId())).thenReturn(PROGRAM);
    when(budgetLineItemService.get(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId())).thenReturn(new BudgetLineItem());

    spyRequisitionService.initiate(FACILITY, PROGRAM, 1L, false, null);

    verify(requisitionEventService).notifyForStatusChange(requisition);
  }

  @Test
  public void shouldNotifyStatusChangeOnAuthorizeAndSendEmailToActiveUsers() throws Exception {
    ArrayList<User> emptyList = new ArrayList<>();
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(calculationService).perform(savedRnr, template);

    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);

    User activeUser = make(a(UserBuilder.defaultUser, with(UserBuilder.active, true)));
    User inactiveUser = make(a(UserBuilder.defaultUser, with(UserBuilder.active, false)));
    List<User> users = asList(activeUser, activeUser, inactiveUser);

    when(savedRnr.getProgram()).thenReturn(submittedRnr.getProgram());
    when(savedRnr.getFacility()).thenReturn(submittedRnr.getFacility());
    when(savedRnr.getSupervisoryNodeId()).thenReturn(1L);
    when(userService.getUsersWithRightInNodeForProgram(submittedRnr.getProgram(), new SupervisoryNode(1L),
      APPROVE_REQUISITION)).thenReturn(users);
    when(supervisoryNodeService.getFor(submittedRnr.getFacility(), submittedRnr.getProgram())).thenReturn(
      new SupervisoryNode(1L));

    requisitionService.authorize(submittedRnr);

    verify(requisitionEventService).notifyForStatusChange(savedRnr);
//    verify(statusChangeEventService).notifyUsers(emptyList, savedRnr.getId(), submittedRnr.getFacility(),
//      submittedRnr.getProgram(), submittedRnr.getPeriod(), "AUTHORIZED");
  }

  @Test
  public void shouldSetDefaultApprovedQuantityOnAuthorization() throws Exception {
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(submittedRnr, AUTHORIZE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);
    doNothing().when(calculationService).perform(savedRnr, template);

    when(supervisoryNodeService.getApproverFor(FACILITY, PROGRAM)).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(FACILITY, PROGRAM)).thenReturn(approverNode);
    doNothing().when(savedRnr).setFieldsForApproval();

    requisitionService.authorize(submittedRnr);

    verify(savedRnr).setFieldsForApproval();
  }

  @Test
  public void shouldNotifyStatusChangeOnSubmit() throws Exception {
    ArrayList<User> emptyList = new ArrayList<>();
    Rnr savedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);

    when(rnrTemplateService.fetchProgramTemplate(PROGRAM.getId())).thenReturn(template);

    doNothing().when(calculationService).perform(savedRnr, template);

    when(savedRnr.getFacility()).thenReturn(initiatedRnr.getFacility());
    when(savedRnr.getProgram()).thenReturn(initiatedRnr.getProgram());
    when(supervisoryNodeService.getFor(initiatedRnr.getFacility(), initiatedRnr.getProgram())).thenReturn(
      new SupervisoryNode(1L));
    when(userService.getUsersWithRightInHierarchyUsingBaseNode(1L, initiatedRnr.getProgram(),
      AUTHORIZE_REQUISITION)).thenReturn(EMPTY_LIST);
    when(userService.getUsersWithRightInNodeForProgram(eq(initiatedRnr.getProgram()), any(SupervisoryNode.class),
      eq(AUTHORIZE_REQUISITION))).thenReturn(EMPTY_LIST);
    when(rnrTemplateService.fetchAllRnRColumns(PROGRAM.getId())).thenReturn(rnrColumns);

    requisitionService.submit(initiatedRnr);

    verify(requisitionEventService).notifyForStatusChange(savedRnr);
//    verify(statusChangeEventService).notifyUsers(emptyList, savedRnr.getId(), savedRnr.getFacility(),
//      savedRnr.getProgram(), savedRnr.getPeriod(), "SUBMITTED");
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
    final Rnr requisition = spy(authorizedRnr);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(requisition);
    List<Rnr> requisitionList = new ArrayList<Rnr>() {{
      add(requisition);
    }};

    when(facilityService.getById(requisition.getFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(requisition.getPeriod().getId())).thenReturn(PERIOD);
    when(programService.getById(requisition.getProgram().getId())).thenReturn(PROGRAM);

    requisitionService.releaseRequisitionsAsOrder(requisitionList, USER_ID);

    verify(requisition).convertToOrder(USER_ID);
  }

  @Test
  public void shouldNotifyStatusChangeToReleased() throws Exception {
    when(requisitionPermissionService.hasPermission(USER_ID, CONVERT_TO_ORDER)).thenReturn(true);
    final Rnr requisition = spy(authorizedRnr);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(requisition);
    List<Rnr> requisitionList = new ArrayList<Rnr>() {{
      add(requisition);
    }};

    when(facilityService.getById(requisition.getFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(requisition.getPeriod().getId())).thenReturn(PERIOD);
    when(programService.getById(requisition.getProgram().getId())).thenReturn(PROGRAM);

    requisitionService.releaseRequisitionsAsOrder(requisitionList, USER_ID);

    verify(requisitionEventService).notifyForStatusChange(requisition);
  }


  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreCreatorEditableBasedOnRnrStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(initiatedRnr, CREATE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(savedRequisition.getProgram().getId(),
      new ArrayList<RegimenColumn>());
    List<ProgramProduct> programProductsList = asList(new ProgramProduct());

    doNothing().when(savedRequisition).copyCreatorEditableFields(initiatedRnr, template, regimenTemplate,
      programProductsList);
    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);
    Mockito.when(programProductService.getNonFullSupplyProductsForProgram(PROGRAM)).thenReturn(programProductsList);
    Mockito.when(regimenColumnService.getRegimenTemplateByProgramId(initiatedRnr.getProgram().getId())).thenReturn(
      regimenTemplate);

    requisitionService.save(initiatedRnr);

    verify(savedRequisition).copyCreatorEditableFields(initiatedRnr, template, regimenTemplate, programProductsList);
    verify(requisitionRepository).update(savedRequisition);
    verify(programProductService).getNonFullSupplyProductsForProgram(PROGRAM);
  }

  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreApproverEditableBasedOnRnrStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(authorizedRnr,
      APPROVE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);
    doNothing().when(savedRequisition).copyApproverEditableFields(authorizedRnr, template);

    requisitionService.save(authorizedRnr);

    verify(savedRequisition).copyApproverEditableFields(authorizedRnr, template);
    verify(requisitionRepository).update(savedRequisition);
  }

  @Test
  public void shouldSaveRnrWithOnlyThoseFieldsWhichAreApproverEditableBasedInApprovalStatus() throws Exception {
    Rnr savedRequisition = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(inApprovalRnr,
      APPROVE_REQUISITION);
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    when(rnrTemplateService.fetchProgramTemplate(savedRequisition.getProgram().getId())).thenReturn(template);
    doNothing().when(savedRequisition).copyApproverEditableFields(inApprovalRnr, template);
    requisitionService.save(inApprovalRnr);

    verify(savedRequisition).copyApproverEditableFields(inApprovalRnr, template);
    verify(requisitionRepository).update(savedRequisition);
  }


  @Test
  public void shouldGetLWRnrById() throws Exception {
    Rnr expectedRnr = new Rnr();
    Long rnrId = 1L;
    Mockito.when(requisitionRepository.getLWById(rnrId)).thenReturn(expectedRnr);
    Rnr returnedRnr = requisitionService.getLWById(rnrId);
    assertThat(returnedRnr, is(expectedRnr));
  }

  @Test
  public void shouldNotSubmittedIfRnrAlreadySubmitted() throws Exception {
    Rnr submittedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(this.submittedRnr, CREATE_REQUISITION);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_SUBMISSION_ERROR);

    requisitionService.submit(submittedRnr);
  }

  @Test
  public void shouldNotAuthorizeIfRnrAlreadyAuthorized() throws Exception {
    Rnr authorizedRnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(this.authorizedRnr,
      AUTHORIZE_REQUISITION);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_AUTHORIZATION_ERROR);

    requisitionService.authorize(authorizedRnr);
  }

  @Test
  public void shouldGetCurrentPeriodForFacilityAndProgram() {
    Date programStartDate = new Date();
    when(programService.getProgramStartDate(1L, 2L)).thenReturn(programStartDate);
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, 1L),
      with(programIdProperty, 2L)));
    requisitionService.getCurrentPeriod(criteria);

    verify(processingScheduleService).getCurrentPeriod(1L, 2L, programStartDate);
  }

  @Test
  public void shouldGetApprovedRequisitionsBySearchCriteriaAndPageNumber() throws Exception {
    String searchType = RequisitionService.SEARCH_ALL;
    String searchVal = "test";
    Integer pageNumber = 1;
    Integer pageSize = 3;
    String sortBy = "sortBy";
    String sortDirection = "asc";
    Rnr rnr = getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(make(a(defaultRequisition)),
      CONVERT_TO_ORDER);
    List<Rnr> filteredRnrList = Arrays.asList(rnr);

    when(requisitionRepository.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal, pageNumber,
      pageSize, 1l, CONVERT_TO_ORDER, sortBy, sortDirection)).thenReturn(filteredRnrList);
    when(staticReferenceDataService.getPropertyValue(CONVERT_TO_ORDER_PAGE_SIZE)).thenReturn(pageSize.toString());

    List<Rnr> rnrList = requisitionService.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal,
      pageNumber, 6, 1l, CONVERT_TO_ORDER, sortBy, sortDirection);

    verify(requisitionRepository).getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal, pageNumber,
      pageSize, 1l, CONVERT_TO_ORDER, sortBy, sortDirection);
    assertThat(rnrList, is(filteredRnrList));
  }

  @Test
  public void shouldThrowErrorInCasePageNumberRequestedNotAvailable() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.page.not.found");

    String sortDirection = "asc";
    String sortBy = "program";
    requisitionService.getApprovedRequisitionsForCriteriaAndPageNumber("searchType", "searchVal", 4, 1, 1l,
      CONVERT_TO_ORDER, sortBy, sortDirection);
  }

  @Test
  public void shouldReturnEmptyListInCaseNotRequisitionsExistAndPage1Requested() throws Exception {
    String sortDirection = "asc";
    String sortBy = "program";
    List<Rnr> requisitions = requisitionService.getApprovedRequisitionsForCriteriaAndPageNumber("searchType",
      "searchVal",
      1, 0, 1l, CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat(requisitions.size(), is(0));
  }


  @Test
  public void shouldGetCountOfApprovedRequisitions() throws Exception {

    int numberOfApprovedRequisitions = 5;
    String searchType = "searchType";
    String searchVal = "search";
    when(requisitionRepository.getCountOfApprovedRequisitionsForCriteria(searchType, searchVal, 1l,
      CONVERT_TO_ORDER)).thenReturn(numberOfApprovedRequisitions);
    Integer pageSize = 3;
    when(staticReferenceDataService.getPropertyValue(CONVERT_TO_ORDER_PAGE_SIZE)).thenReturn(pageSize.toString());

    Integer count = requisitionService.getNumberOfPagesOfApprovedRequisitionsForCriteria(searchType, searchVal, 1l,
      CONVERT_TO_ORDER);

    assertThat(count, is(2));
  }

  @Test
  public void shouldReturn2pagesFor6RequisitionsAndPageSize3() throws Exception {
    int numberOfApprovedRequisitions = 6;
    String searchType = "searchType";
    String searchVal = "search";
    when(requisitionRepository.getCountOfApprovedRequisitionsForCriteria(searchType, searchVal, 1l,
      CONVERT_TO_ORDER)).thenReturn(numberOfApprovedRequisitions);
    Integer pageSize = 3;
    when(staticReferenceDataService.getPropertyValue(CONVERT_TO_ORDER_PAGE_SIZE)).thenReturn(pageSize.toString());

    Integer count = requisitionService.getNumberOfPagesOfApprovedRequisitionsForCriteria(searchType, searchVal, 1l,
      CONVERT_TO_ORDER);

    assertThat(count, is(2));
  }

  @Test
  public void shouldGetEmptyListIfEmptyPeriodListAndNonEmergency() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria();
    criteria.setEmergency(false);

    assertThat(requisitionService.getRequisitionsFor(criteria, new ArrayList<ProcessingPeriod>()).size(), is(0));
  }

  @Test
  public void shouldGetEmptyListIfNullPeriodListAndNonEmergency() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria();
    criteria.setEmergency(false);

    assertThat(requisitionService.getRequisitionsFor(criteria, null).size(), is(0));
  }

  @Test
  public void shouldSetPeriodIdOfFirstPeriodOfPeriodListForNonEmergency() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria();
    criteria.setEmergency(false);
    ProcessingPeriod processingPeriod = new ProcessingPeriod(3l);

    requisitionService.getRequisitionsFor(criteria, asList(processingPeriod, new ProcessingPeriod(67l)));

    assertThat(criteria.getPeriodId(), is(3l));
  }

  @Test
  public void shouldNotSetPeriodIdOfFirstPeriodOfPeriodListForEmergency() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria();
    criteria.setEmergency(true);
    ProcessingPeriod processingPeriod = new ProcessingPeriod(3l);

    requisitionService.getRequisitionsFor(criteria, asList(processingPeriod, new ProcessingPeriod(67l)));

    assertThat(criteria.getPeriodId(), is(nullValue()));
  }

  @Test
  public void shouldSetCalcStrategyForEmergencyRnr() throws Exception {
    Long requisitionId = 1L;
    Rnr emergencyRequisition = spy(new Rnr());
    emergencyRequisition.setFacility(FACILITY);
    emergencyRequisition.setProgram(PROGRAM);
    emergencyRequisition.setPeriod(PERIOD);
    emergencyRequisition.setId(requisitionId);
    emergencyRequisition.setEmergency(true);
    when(requisitionRepository.getById(requisitionId)).thenReturn(emergencyRequisition);
    Mockito.doNothing().when(emergencyRequisition).fillBasicInformation(any(Facility.class), any(Program.class),
      any(ProcessingPeriod.class));

    Rnr result = requisitionService.getFullRequisitionById(emergencyRequisition.getId());

    verify(requisitionRepository).getById(emergencyRequisition.getId());

    assertThat(result, is(emergencyRequisition));
  }

  @Test
  public void shouldGetPeriodCurrentPeriodIfEmergency() throws Exception {
    Date programStartDate = new Date();
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(processingScheduleService.getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate)).thenReturn(
      expectedPeriod);

    ProcessingPeriod actualPeriod = requisitionService.findPeriod(FACILITY, PROGRAM, true);

    verify(processingScheduleService).getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate);
    assertThat(actualPeriod, is(expectedPeriod));
  }

  @Test
  public void shouldGetPeriodCurrentPeriodIfFacilityVirtual() throws Exception {
    Date programStartDate = new Date();
    FACILITY.setVirtualFacility(true);
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(processingScheduleService.getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate)).thenReturn(
      expectedPeriod);

    ProcessingPeriod actualPeriod = requisitionService.findPeriod(FACILITY, PROGRAM, false);

    verify(processingScheduleService).getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate);
    assertThat(actualPeriod, is(expectedPeriod));
  }

  @Test
  public void shouldGetLastValidPeriodInCaseFacilityNotVirtualAndRnrNotEmergency() throws Exception {
    RequisitionService service = spy(requisitionService);
    doReturn(PERIOD).when(service).getPeriodForInitiating(FACILITY, PROGRAM);

    ProcessingPeriod actualPeriod = service.findPeriod(FACILITY, PROGRAM, false);

    assertThat(actualPeriod, is(PERIOD));
  }

  @Test
  public void shouldThrowErrorIfPeriodNotDefined() throws Exception {
    when(processingScheduleService.getCurrentPeriod(eq(FACILITY.getId()), eq(PROGRAM.getId()),
      any(Date.class))).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.program.configuration.missing");

    requisitionService.findPeriod(FACILITY, PROGRAM, true);
  }

  @Test
  public void shouldGetPeriodForInitiatingRequisition() throws Exception {
    Date programStartDate = new Date();
    Long startingPeriod = 3l;
    RequisitionService service = spy(requisitionService);
    when(requisitionRepository.getLastRegularRequisition(FACILITY, PROGRAM)).thenReturn(
      make(a(defaultRequisition, with(periodId, startingPeriod), with(status, AUTHORIZED))));
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);
    when(processingScheduleService.getCurrentPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate)).thenReturn(
      new ProcessingPeriod(5l));
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate,
      startingPeriod)).thenReturn(asList(PERIOD));

    ProcessingPeriod period = service.getPeriodForInitiating(FACILITY, PROGRAM);

    assertThat(period, is(PERIOD));
  }

  @Test
  public void shouldAllowCreatingRnrIfPreviousRequisitionsDoNotExist() throws Exception {
    Date programStartDate = new Date();
    when(requisitionRepository.getLastRegularRequisition(FACILITY, PROGRAM)).thenReturn(null);
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate,
      null)).thenReturn(asList(PERIOD, new ProcessingPeriod()));

    ProcessingPeriod period = requisitionService.getPeriodForInitiating(FACILITY, PROGRAM);

    assertThat(period, is(PERIOD));
  }

  @Test
  public void shouldThrowErrorIfNoPeriodExistsForInitiating() throws Exception {
    Date programStartDate = new Date();
    when(requisitionRepository.getLastRegularRequisition(FACILITY, PROGRAM)).thenReturn(null);
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), programStartDate,
      null)).thenReturn(new ArrayList<ProcessingPeriod>());

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.program.configuration.missing");

    requisitionService.getPeriodForInitiating(FACILITY, PROGRAM);
  }

  @Test
  public void shouldThrowErrorIfLastRegularRequisitionExistsAndIsPreAuthorized() {
    Date programStartDate = new Date();
    Rnr requisition = new Rnr();
    requisition.setStatus(INITIATED);
    when(requisitionRepository.getLastRegularRequisition(FACILITY, PROGRAM)).thenReturn(requisition);
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(programStartDate);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.previous.not.filled");

    requisitionService.getPeriodForInitiating(FACILITY, PROGRAM);

  }

  @Test
  public void shouldGetFacilityIdFromRnrId() throws Exception {
    Mockito.when(requisitionRepository.getFacilityId(1L)).thenReturn(1L);
    assertThat(requisitionService.getFacilityId(1L), is(1L));
  }

  @Test
  public void shouldFillReportingDaysWhileInitiatingRnr() throws Exception {
    List<FacilityTypeApprovedProduct> facilityApprovedProducts = mock(List.class);
    RegimenTemplate regimenTemplate = mock(RegimenTemplate.class);
    List<Regimen> regimens = mock(List.class);

    Rnr previousRnr = new Rnr();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(3L);
    Program requisitionProgram = new Program(1234L);
    requisitionProgram.setBudgetingApplies(true);
    requisitionProgram.setPush(false);
    requisitionProgram.setIsEquipmentConfigured(false);
      requisitionProgram.setUsePriceSchedule(false);

    RequisitionService spyRequisitionService = spy(requisitionService);

    when(
      requisitionPermissionService.hasPermission(USER_ID, FACILITY, requisitionProgram, CREATE_REQUISITION)).thenReturn(
      true);
    doReturn(PERIOD).when(spyRequisitionService).findPeriod(FACILITY, requisitionProgram, false);
    when(regimenService.getByProgram(requisitionProgram.getId())).thenReturn(regimens);
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(),
      requisitionProgram.getId())).thenReturn(facilityApprovedProducts);
    when(regimenColumnService.getRegimenTemplateByProgramId(requisitionProgram.getId())).thenReturn(regimenTemplate);
    ProgramRnrTemplate rnrTemplate = new ProgramRnrTemplate(getRnrColumns());
    when(rnrTemplateService.fetchProgramTemplateForRequisition(requisitionProgram.getId())).thenReturn(rnrTemplate);

    when(requisitionRepository.getRegularRequisitionWithLineItems(FACILITY, requisitionProgram,
      previousPeriod)).thenReturn(previousRnr);
    Date createdDateFromDB = DateTime.now().toDate();
    when(dbMapper.getCurrentTimeStamp()).thenReturn(createdDateFromDB);
    Rnr requisition = mock(Rnr.class);
    when(requisition.getId()).thenReturn(1l);
    when(requisition.getFacility()).thenReturn(FACILITY);
    when(requisition.getProgram()).thenReturn(requisitionProgram);
    when(requisition.getPeriod()).thenReturn(PERIOD);
    when(requisition.getStatus()).thenReturn(INITIATED);

    whenNew(Rnr.class).withArguments(FACILITY, requisitionProgram, PERIOD, false, facilityApprovedProducts, regimens,
      USER_ID).thenReturn(requisition);
    when(requisitionRepository.getById(1l)).thenReturn(requisition);
    when(budgetLineItemService.get(FACILITY.getId(), requisitionProgram.getId(), PERIOD.getId())).thenReturn(
      new BudgetLineItem());
    when(programService.getById(requisitionProgram.getId())).thenReturn(requisitionProgram);

    spyRequisitionService.initiate(FACILITY, requisitionProgram, USER_ID, false, null);

    verify(calculationService).fillReportingDays(requisition);
    verify(requisition).setCreatedDate(createdDateFromDB);
  }

  @Test
  public void shouldThrowErrorIfRegimenLineItemsAreNotValidWhileSubmittingRnr() throws NoSuchFieldException, IllegalAccessException {
    Rnr rnr = new Rnr(1l);
    rnr.setStatus(INITIATED);
    rnr.setProgram(new Program(2l));
    RegimenLineItem regimenLineItem = mock(RegimenLineItem.class);
    doThrow(new DataException("Invalid regimen line item")).when(regimenLineItem).validate(any(RegimenTemplate.class));
    rnr.setRegimenLineItems(asList(regimenLineItem));
    RequisitionService spyRequisitionService = spy(requisitionService);
    doReturn(rnr).when(spyRequisitionService).getFullRequisitionById(rnr.getId());
    RegimenTemplate regimenTemplate = mock(RegimenTemplate.class);
    when(regimenColumnService.getRegimenTemplateByProgramId(rnr.getProgram().getId())).thenReturn(regimenTemplate);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid regimen line item");

    spyRequisitionService.submit(rnr);
  }

  @Test
  public void shouldThrowErrorIfRegimenLineItemsAreNotValidWhileAuthorizingRnr() throws NoSuchFieldException, IllegalAccessException {
    Rnr rnr = new Rnr(1l);
    rnr.setStatus(SUBMITTED);
    rnr.setProgram(new Program(2l));
    RegimenLineItem regimenLineItem = mock(RegimenLineItem.class);
    doThrow(new DataException("Invalid regimen line item")).when(regimenLineItem).validate(any(RegimenTemplate.class));
    rnr.setRegimenLineItems(asList(regimenLineItem));
    RequisitionService spyRequisitionService = spy(requisitionService);
    doReturn(rnr).when(spyRequisitionService).getFullRequisitionById(rnr.getId());
    RegimenTemplate regimenTemplate = mock(RegimenTemplate.class);
    when(regimenColumnService.getRegimenTemplateByProgramId(rnr.getProgram().getId())).thenReturn(regimenTemplate);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid regimen line item");

    spyRequisitionService.authorize(rnr);
  }

  @Test
  public void shouldGetLineItemWithRnrIdAndProductCode() throws Exception {
    RnrLineItem expectedLineItem = new RnrLineItem();
    when(requisitionRepository.getNonSkippedLineItem(3L, "P100")).thenReturn(expectedLineItem);

    RnrLineItem lineItem = requisitionService.getNonSkippedLineItem(3L, "P100");

    assertThat(lineItem, is(expectedLineItem));
  }

  @Test
  public void shouldUpdateClientSubmittedFields() throws Exception {
    Rnr rnr = new Rnr();
    requisitionService.updateClientFields(rnr);
    verify(requisitionRepository).updateClientFields(rnr);
  }

  @Test
  public void shouldInsertPatientQuantificationLineItems() throws Exception {
    Rnr rnr = new Rnr();
    requisitionService.insertPatientQuantificationLineItems(rnr);
    verify(requisitionRepository).insertPatientQuantificationLineItems(rnr);
  }

  @Test
  public void shouldInsertRnRSignature(){
    Rnr rnr = new Rnr();
    requisitionService.insertRnrSignatures(rnr);
    verify(requisitionRepository).insertRnrSignatures(rnr);
  }

  @Test
  public void shouldGetRequisitionsByFacility() {
    Facility facility = new Facility();
    requisitionService.getRequisitionsByFacility(facility);
    verify(requisitionRepository).getRequisitionDetailsByFacility(facility);
  }

  private void setupForInitRnr() {
    when(requisitionPermissionService.hasPermission(USER_ID, FACILITY, PROGRAM, CREATE_REQUISITION)).thenReturn(true);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM.getId())).thenReturn(
      new ProgramRnrTemplate(getRnrColumns()));
    when(regimenColumnService.getRegimenTemplateByProgramId(PROGRAM.getId())).thenReturn(new RegimenTemplate());
  }

  private Rnr getFilledSavedRequisitionWithDefaultFacilityProgramPeriod(Rnr rnr, String rightName) {
    Rnr savedRnr = spy(rnr);
    doNothing().when(savedRnr).calculateForApproval();
    when(requisitionPermissionService.hasPermissionToSave(USER_ID, savedRnr)).thenReturn(true);
    when(requisitionPermissionService.hasPermission(USER_ID, savedRnr, rightName)).thenReturn(true);
    when(programService.getById(savedRnr.getProgram().getId())).thenReturn(PROGRAM);
    when(facilityService.getById(savedRnr.getFacility().getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(savedRnr.getProgram().getId())).thenReturn(PERIOD);
    when(requisitionRepository.getById(rnr.getId())).thenReturn(savedRnr);
    return savedRnr;
  }

  private Rnr createRequisition(Long periodId, RnrStatus status) {
    Facility defaultFacility = make(a(FacilityBuilder.defaultFacility));
    defaultFacility.setId(1L);
    return make(a(RequisitionBuilder.defaultRequisition,
      with(RequisitionBuilder.facility, defaultFacility),
      with(RequisitionBuilder.periodId, periodId),
      with(RequisitionBuilder.status, status)));
  }

  private ProcessingPeriod createProcessingPeriod(Long id, DateTime startDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.startDate, startDate.toDate())));
    processingPeriod.setId(id);
    return processingPeriod;
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
}

