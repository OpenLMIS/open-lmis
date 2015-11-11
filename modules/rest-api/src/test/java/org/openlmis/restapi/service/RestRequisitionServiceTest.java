/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.service;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.builder.ReportBuilder;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.builder.PatientQuantificationsBuilder;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.PROGRAM_ID;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.restapi.builder.ReportBuilder.*;
import static org.openlmis.rnr.builder.RegimenLineItemBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.remarks;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RestRequisitionService.class, ReplenishmentDTO.class})
public class RestRequisitionServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionService requisitionService;
  @Mock
  UserService userService;
  @Mock
  FacilityApprovedProductService facilityApprovedProductService;
  @InjectMocks
  RestRequisitionService service;
  Rnr requisition;
  Report report;
  User user;
  byte[] encodedCredentialsBytes;
  String validProductCode;
  RnrLineItem rnrLineItem;
  @Mock
  private OrderService orderService;
  @Mock
  private FacilityService facilityService;
  @Mock
  private RnrTemplateService rnrTemplateService;
  @Mock
  private RestRequisitionCalculator restRequisitionCalculator;
  @Mock
  private ProcessingPeriodService periodService;
  @Mock
  private ProgramService programService;
  @Mock
  private ProductService productService;

  private Facility facility;

  @Before
  public void setUp() throws Exception {
    validProductCode = "validProductCode";
    rnrLineItem = new RnrLineItem();
    rnrLineItem.setProductCode(validProductCode);
    report = make(a(defaultReport));
    String encodedCredentials = "1:correct token";
    requisition = new Rnr();
    requisition.setId(2L);
    user = new User();
    user.setId(1L);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);
    mockStatic(Base64.class);
    encodedCredentialsBytes = encodedCredentials.getBytes();
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    setUpRequisitionReportBeforeSubmit();

    RegimenLineItem reportRegimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
    report.setRegimens(asList(reportRegimenLineItem));
    service.submitReport(report, 1L);

    verify(facilityService).getOperativeFacilityByCode(DEFAULT_AGENT_CODE);
    verify(programService).getValidatedProgramByCode(DEFAULT_PROGRAM_CODE);
    verify(requisitionService).initiate(facility, new Program(PROGRAM_ID), 1L, false, null);
    verify(requisitionService).submit(requisition);
    assertThat(requisition.getRegimenLineItems().get(0).getPatientsOnTreatment(), is(10));
    assertThat(requisition.getRegimenLineItems().get(0).getPatientsStoppedTreatment(), is(5));
  }


  @Test
  public void shouldUpdateClientSubmittedNotesIfExists() throws Exception {
    setUpRequisitionReportBeforeSubmit();

    report.setClientSubmittedNotes("xyz");
    service.submitReport(report, 1L);

    verify(requisitionService).updateClientFields(requisition);
    assertEquals("xyz", requisition.getClientSubmittedNotes());
  }

  @Test
  public void shouldUpdateClientSubmittedTimeWhenTimeIsSet() throws
          Exception {
    setUpRequisitionReportBeforeSubmit();

    report.setClientSubmittedTime("2015-09-10 12:00:00");
    service.submitReport(report, 1L);
    verify(requisitionService, times(1)).updateClientFields(requisition);
  }

  private void setUpRequisitionReportBeforeSubmit() throws Exception {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, "P10")));
    List<RnrLineItem> products = asList(rnrLineItem);
    requisition.setFullSupplyLineItems(products);
    requisition.setProgram(new Program());

    when(facilityApprovedProductService.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(any(Long.class), any(Long.class))).thenReturn(new ArrayList<FacilityTypeApprovedProduct>());

    RegimenLineItem regimenLineItem = make(a(defaultRegimenLineItem));
    requisition.setRegimenLineItems(asList(regimenLineItem));


    report.setProducts(products);
    Long facility_id = 5L;

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    facility = make(a(defaultFacility, with(facilityId, facility_id), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));

    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));
    when(requisitionService.initiate(facility, new Program(PROGRAM_ID), user.getId(), false, null)).thenReturn(requisition);
    when(requisitionService.save(requisition)).thenReturn(requisition);
    when(productService.getByCode(validProductCode)).thenReturn(new Product());
    Rnr reportedRequisition = mock(Rnr.class);
    whenNew(Rnr.class).withArguments(requisition.getId()).thenReturn(reportedRequisition);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));

    when(requisitionService.submit(requisition)).thenReturn(requisition);
  }


  @Test
  public void shouldInsertPatientQuantificationWhenReportHasData() throws Exception {

    setUpRequisitionReportBeforeSubmit();

    List<PatientQuantificationLineItem> patientQuantifications = new PatientQuantificationsBuilder().addLineItem(new PatientQuantificationLineItem("newborn", new Integer(10))).
            addLineItem(new PatientQuantificationLineItem("adults", new Integer(5))).build();

    RegimenLineItem reportRegimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
    report.setRegimens(asList(reportRegimenLineItem));
    report.setPatientQuantifications(patientQuantifications);
    service.submitReport(report, 1L);

    assertThat(requisition.getPatientQuantifications().get(0).getTotal(), is(10));
    assertThat(requisition.getPatientQuantifications().get(1).getTotal(), is(5));
    verify(requisitionService).insertPatientQuantificationLineItems(requisition);
  }

  @Test
  public void sdpShouldCreateAndSubmitARequisition() throws Exception{
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, "P10")));
    List<RnrLineItem> products = asList(rnrLineItem);
    requisition.setFullSupplyLineItems(products);
    requisition.setProgram(new Program());

    RegimenLineItem regimenLineItem = make(a(defaultRegimenLineItem));
    requisition.setRegimenLineItems(asList(regimenLineItem));


    report.setProducts(products);
    RegimenLineItem reportRegimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
    report.setRegimens(asList(reportRegimenLineItem));
    report.setPeriodId(1L);
    report.setEmergency(false);

    Long facility_id = 5L;

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    Facility facility = make(a(defaultFacility, with(facilityId, facility_id), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));

    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));
    when(requisitionService.initiate(facility, new Program(PROGRAM_ID), user.getId(), false, null)).thenReturn(requisition);
    when(requisitionService.save(requisition)).thenReturn(requisition);
    when(productService.getByCode(validProductCode)).thenReturn(new Product());
    Rnr reportedRequisition = mock(Rnr.class);
    whenNew(Rnr.class).withArguments(requisition.getId()).thenReturn(reportedRequisition);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));

    when(requisitionService.submit(requisition)).thenReturn(requisition);

    service.submitSdpReport(report, 1L);

    verify(facilityService).getOperativeFacilityByCode(DEFAULT_AGENT_CODE);
    verify(programService).getValidatedProgramByCode(DEFAULT_PROGRAM_CODE);
    verify(requisitionService).initiate(facility, new Program(PROGRAM_ID), 1L, false, null);
    verify(requisitionService).submit(requisition);
    assertThat(requisition.getRegimenLineItems().get(0).getPatientsOnTreatment(), is(10));
    assertThat(requisition.getRegimenLineItems().get(0).getPatientsStoppedTreatment(), is(5));
  }

  @Test
  public void sdpShouldNotInitiateRnrIfRnrAlreadyExists() throws Exception{

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, "P10")));
    List<RnrLineItem> products = asList(rnrLineItem);
    requisition.setFullSupplyLineItems(products);
    requisition.setProgram(new Program());

    RegimenLineItem regimenLineItem = make(a(defaultRegimenLineItem));
    requisition.setRegimenLineItems(asList(regimenLineItem));


    report.setProducts(products);
    RegimenLineItem reportRegimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
    report.setRegimens(asList(reportRegimenLineItem));
    report.setPeriodId(1L);
    report.setEmergency(false);

    Long facility_id = 5L;

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    Facility facility = make(a(defaultFacility, with(facilityId, facility_id), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));

    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));

    expectedException.expect(DataException.class);
    doThrow(new DataException("rnr.error")).when(restRequisitionCalculator).validateCustomPeriod(any(Facility.class), any(Program.class), any(ProcessingPeriod.class), any(Long.class));

    ArrayList<ProcessingPeriod> array = new ArrayList<>();
    when(requisitionService.getRequisitionsFor(any(RequisitionSearchCriteria.class), any(array.getClass()))).thenReturn(asList(new Rnr()));

    service.submitSdpReport(report, 1L);
    verify(requisitionService, never()).initiate(any(Facility.class), any(Program.class), any(Long.class), any(Boolean.class), any(ProcessingPeriod.class));

  }

  @Test
  public void shouldThrowErrorIfPeriodValidationFails() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr.error");

    doThrow(new DataException("rnr.error")).when(restRequisitionCalculator).validatePeriod(any(Facility.class), any(Program.class));

    service.submitReport(report, 1l);

    verify(requisitionService, never()).initiate(any(Facility.class), any(Program.class), any(Long.class), any(Boolean.class),any(ProcessingPeriod.class));
    verify(requisitionService, never()).save(any(Rnr.class));
    verify(requisitionService, never()).submit(any(Rnr.class));
  }

  @Test
  public void sdpShouldThrowErrorIfPeriodValidationFails() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr.error");
    report.setPeriodId(2L);
    when(programService.getValidatedProgramByCode(anyString())).thenReturn(new Program());
    when(facilityService.getOperativeFacilityByCode(anyString())).thenReturn(new Facility());
    doThrow(new DataException("rnr.error")).when(restRequisitionCalculator).validateCustomPeriod(any(Facility.class), any(Program.class), any(ProcessingPeriod.class), any(Long.class));

    service.submitSdpReport(report, 1l);

    verify(requisitionService, never()).initiate(any(Facility.class), any(Program.class), any(Long.class), any(Boolean.class), any(ProcessingPeriod.class));
    verify(requisitionService, never()).save(any(Rnr.class));
    verify(requisitionService, never()).submit(any(Rnr.class));
  }

  @Test
  public void shouldThrowErrorIfProductValidationFails() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr.error");

    doThrow(new DataException("rnr.error")).when(restRequisitionCalculator).validateProducts(any(List.class), any(Rnr.class));

    service.submitReport(report, 1L);

    verify(requisitionService, never()).save(any(Rnr.class));
    verify(requisitionService, never()).submit(any(Rnr.class));
  }

  @Test
  public void shouldThrowErrorIfInvalidRequisitionIdPassed() throws Exception {
    Long reportRequisitionId = 1L;
    Long userId = 12345L;
    Rnr requisitionFromReport = new Rnr(reportRequisitionId);

    Report spyReport = spy(report);
    when(spyReport.getRequisition(reportRequisitionId, userId)).thenReturn(requisitionFromReport);
    doThrow(new DataException("rnr not found")).when(requisitionService).getFullRequisitionById(requisitionFromReport.getId());

    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr not found");

    service.approve(spyReport, reportRequisitionId, 2L);
  }

  @Test
  public void shouldNotApproveRnrIfDoesNotBelongToVirtualFacility() throws Exception {
    long requisitionId = 11234L;
    Rnr requisitionFromReport = new Rnr(requisitionId);
    long modifiedBy = 233L;

    Facility facility = make(a(defaultFacility, with(virtualFacility, false)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with
        (RequisitionBuilder.facility, facility)));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.approval.not.allowed");

    Report spyReport = spy(report);
    doReturn(requisitionFromReport).when(spyReport).getRequisition(requisitionId, modifiedBy);

    when(requisitionService.getFullRequisitionById(requisitionId)).thenReturn(rnr);

    service.approve(spyReport, requisitionId, modifiedBy);
  }

  @Test
  public void shouldApproveIfRnrBelongsToVirtualFacility() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    Report spyReport = spy(report);
    long requisitionId = 111L;
    long modifiedBy = 233L;

    when(spyReport.getRequisition(requisitionId, modifiedBy)).thenReturn(requisitionFromReport);
    Facility facility = make(a(defaultFacility, with(virtualFacility, true)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with
            (RequisitionBuilder.facility, facility)));
    when(requisitionService.getFullRequisitionById(requisitionFromReport.getId())).thenReturn(rnr);

    service.approve(spyReport, requisitionId, modifiedBy);

    verify(spyReport).getRequisition(requisitionId, modifiedBy);
    verify(requisitionService).save(requisitionFromReport);
    verify(requisitionService).approve(requisitionFromReport, DEFAULT_APPROVER_NAME);
  }

  @Test
  public void shouldSaveReportAsRequisition() throws Exception {
    Program program = new Program();
    report.setProducts(new ArrayList<RnrLineItem>());
    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);

    Rnr rnr = new Rnr();
    rnr.setProgram(program);
    when(requisitionService.initiate(facility, program, 3l, false, null)).thenReturn(rnr);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));
    service.submitReport(report, 3l);

    verify(requisitionService).save(rnr);

  }

  @Test
  public void shouldThrowErrorIfInvalidRegimenIsProvided() throws Exception {
    Program program = new Program();
    report.setProducts(new ArrayList<RnrLineItem>());
    RegimenLineItem reportRegimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
    report.setRegimens(asList(reportRegimenLineItem));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);

    Rnr rnr = new Rnr();
    rnr.setProgram(program);
    when(requisitionService.initiate(facility, program, 3l, false, null)).thenReturn(rnr);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.regimen");

    service.submitReport(report, 3l);

  }

  @Test
  public void shouldThrowAnExceptionIfLineItemsCountMismatchBetweenReportAndSavedRequisition() throws Exception {
    long requisitionId = 123L;
    List<RnrLineItem> productList = asList(make(a(defaultRnrLineItem, with(productCode, "P10"))));
    Report report = make(a(defaultReport, with(products, productList)));

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility)));
    rnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P10"))), make(a(defaultRnrLineItem, with(productCode, "P11")))));

    when(requisitionService.getFullRequisitionById(requisitionId)).thenReturn(rnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.number.of.line.items.mismatch");

    service.approve(report, requisitionId, 3L);
  }

  @Test
  public void shouldGetReplenishmentDTOByRequisitionId() throws Exception {
    Long rnrId = 3L;
    Rnr expectedRnr = new Rnr(rnrId);

    mockStatic(ReplenishmentDTO.class);
    Order order = mock(Order.class);
    when(requisitionService.getFullRequisitionById(rnrId)).thenReturn(expectedRnr);
    when(orderService.getOrder(rnrId)).thenReturn(order);
    ReplenishmentDTO expectedReplenishmentDTO = new ReplenishmentDTO();
    when(ReplenishmentDTO.prepareForREST(expectedRnr, order)).thenReturn(expectedReplenishmentDTO);

    ReplenishmentDTO replenishmentDTO = service.getReplenishmentDetails(rnrId);

    assertThat(replenishmentDTO, is(expectedReplenishmentDTO));
    verify(requisitionService).getFullRequisitionById(rnrId);
  }

  @Test
  public void shouldThrowErrorIfProductCodeIsInvalidWhileSubmit() {
    String invalidProductCode = "Random";
    Long userId = 3l;
    List<RnrLineItem> productList = asList(make(a(defaultRnrLineItem, with(productCode, invalidProductCode))));
    Report report = make(a(defaultReport, with(products, productList)));

    Program program = new Program();
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility)));
    rnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P10"))), make(a(defaultRnrLineItem, with(productCode, "P11")))));
    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);
    when(requisitionService.initiate(facility, program, userId, false, null)).thenReturn(rnr);
    doThrow(new DataException("invalid product codes")).when(restRequisitionCalculator).validateProducts(productList, rnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("invalid product codes");

    service.submitReport(report, userId);
  }

  @Test
  public void shouldSkipItemsNotPresentInReport() throws Exception {
    Program program = new Program(PROGRAM_ID);
    RnrColumn stockInHandColumn = createRnrColumn("stockInHand", true, RnRColumnSource.USER_INPUT);
    ProgramRnrTemplate template = new ProgramRnrTemplate(asList(stockInHandColumn));
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM_ID)).thenReturn(template);

    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P10")));
    RnrLineItem rnrLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    report.setProducts(asList(rnrLineItem1));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);

    Facility reportFacility = make(a(defaultFacility, with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportFacility);

    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with
            (RequisitionBuilder.facility,
                    reportFacility)));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1, rnrLineItem2));

    when(requisitionService.initiate(reportFacility, program, 3l, false, null)).thenReturn(rnr);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));


    service.submitReport(report, 3l);

    assertThat(rnr.getFullSupplyLineItems().get(0).getSkipped(), is(false));
    assertThat(rnr.getFullSupplyLineItems().get(1).getSkipped(), is(true));
  }

  @Test
  public void shouldCopyVisibleUserInputAndNonNullFieldsFromReportToRnrLineItem() throws Exception {
    Program rnrProgram = new Program(PROGRAM_ID);
    RnrColumn stockInHandColumn = createRnrColumn("stockInHand", true, RnRColumnSource.USER_INPUT);
    ProgramRnrTemplate template = new ProgramRnrTemplate(asList(stockInHandColumn));
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM_ID)).thenReturn(template);

    RnrLineItem reportedLineItem = make(a(defaultRnrLineItem, with(productCode, "P10"), with(stockInHand, 100)));
    RnrLineItem initiatedLineItem = make(a(defaultRnrLineItem, with(productCode, "P10"), with(stockInHand, 0)));

    report.setProducts(asList(reportedLineItem));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(rnrProgram);

    Facility reportFacility = make(a(defaultFacility, with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportFacility);

    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with
            (RequisitionBuilder.facility,
                    reportFacility), with(RequisitionBuilder.program, rnrProgram)));
    rnr.setFullSupplyLineItems(asList(initiatedLineItem));

    when(requisitionService.initiate(reportFacility, rnrProgram, 3l, false, null)).thenReturn(rnr);

    service.submitReport(report, 3l);

    assertThat(rnr.getFullSupplyLineItems().get(0).getStockInHand(), is(100));

  }

  @Test
  public void shouldSetFieldsToReportedValuesOtherwiseCopyFromInitiatedRnrLineItem() throws Exception {
    Program rnrProgram = new Program(PROGRAM_ID);

    ProgramRnrTemplate template = new ProgramRnrTemplate(getRnrColumns());
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM_ID)).thenReturn(template);

    RnrLineItem reportedLineItem = new RnrLineItem();
    reportedLineItem.setProductCode("P10");
    reportedLineItem.setQuantityDispensed(100);
    Integer nullInteger = null;
    String nullString = null;
    RnrLineItem initiatedLineItem = make(a(defaultRnrLineItem, with(productCode, "P10"), with(stockInHand, nullInteger),
      with(quantityDispensed, nullInteger), with(beginningBalance, nullInteger), with(quantityReceived, nullInteger),
      with(totalLossesAndAdjustments, 0), with(newPatientCount, 0), with(stockOutDays, 0), with(quantityRequested, nullInteger),
      with(reasonForRequestedQuantity, nullString), with(remarks, nullString),
      with(skipped, false)));

    report.setProducts(asList(reportedLineItem));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(rnrProgram);

    Facility reportFacility = make(a(defaultFacility, with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportFacility);

    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with
            (RequisitionBuilder.facility,
                    reportFacility), with(RequisitionBuilder.program, rnrProgram)));
    rnr.setFullSupplyLineItems(asList(initiatedLineItem));

    when(requisitionService.initiate(reportFacility, rnrProgram, 3l, false, null)).thenReturn(rnr);

    service.submitReport(report, 3l);

    RnrLineItem rnrLineItem = rnr.getFullSupplyLineItems().get(0);
    assertThat(rnrLineItem.getStockInHand(), is(nullValue()));
    assertThat(rnrLineItem.getQuantityDispensed(), is(100));
    assertThat(rnrLineItem.getBeginningBalance(), is(nullValue()));
    assertThat(rnrLineItem.getQuantityReceived(), is(nullValue()));
    assertThat(rnrLineItem.getTotalLossesAndAdjustments(), is(0));
    assertThat(rnrLineItem.getNewPatientCount(), is(0));
    assertThat(rnrLineItem.getStockOutDays(), is(0));
    assertThat(rnrLineItem.getQuantityRequested(), is(nullValue()));
    assertThat(rnrLineItem.getReasonForRequestedQuantity(), is(nullValue()));
    assertThat(rnrLineItem.getRemarks(), is(nullValue()));
    assertThat(rnrLineItem.getSkipped(), is(false));
  }

  @Test
  public void shouldAuthorizeRnr() throws Exception {
    String facilityCode = "agent";
    String programCode = "program";
    Facility facility = new Facility();
    Program program = new Program(1l);
    Report report = make(a(defaultReport, with(agentCode, facilityCode), with(ReportBuilder.programCode, programCode)));
    when(facilityService.getOperativeFacilityByCode(facilityCode)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(programCode)).thenReturn(program);
    Rnr rnr = new Rnr(facility, program, new ProcessingPeriod());
    when(requisitionService.initiate(facility, program, 3l, false, null)).thenReturn(rnr);
    when(requisitionService.submit(rnr)).thenReturn(rnr);
    when(requisitionService.authorize(rnr)).thenReturn(rnr);

    Rnr authorizedRequisition = service.submitReport(report, 3l);

    verify(requisitionService).authorize(rnr);
    assertThat(authorizedRequisition, is(rnr));
  }

  @Test
  public void shouldGetPreviousProcessingPeriodsForRnr() {
    String facilityCode = "agent";
    String programCode = "program";
    Facility facility = new Facility();
    facility.setVirtualFacility(true);
    Program program = new Program(1l);
    Report report = make(a(defaultReport, with(agentCode, facilityCode), with(ReportBuilder.programCode, programCode)));
    when(facilityService.getOperativeFacilityByCode(facilityCode)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(programCode)).thenReturn(program);
    Rnr initiatedRnr = mock(Rnr.class);
    when(requisitionService.initiate(facility, program, 3l, false, null)).thenReturn(initiatedRnr);
    when(initiatedRnr.getFullSupplyLineItems()).thenReturn(new ArrayList<RnrLineItem>());
    when(initiatedRnr.getProgram()).thenReturn(program);

    service.submitReport(report, 3L);

    verify(restRequisitionCalculator).setDefaultValues(initiatedRnr);
  }

  @Test
  public void shouldThrowExceptionIfFacilityCodeIsInvalid() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.facility.unknown");
    service.getRequisitionsByFacility("invalid_code");
  }

  @Test
  public void shouldGetRequisitionsIfFacilityIsValid() {
    Facility facility = new Facility();
    facility.setCode(FACILITY_CODE);
    facility.setId(120L);

    when(facilityService.getFacilityByCode(FACILITY_CODE)).thenReturn(facility);

    service.getRequisitionsByFacility(FACILITY_CODE);

    verify(requisitionService).getRequisitionsByFacility(facility);
  }

  @Test
  public void shouldSaveSignaturesForRequisition() throws Exception {
    setUpRequisitionReportBeforeSubmit();
    Signature submitterSignature = new Signature(Signature.Type.SUBMITTER, "Mystique");
    Signature approverSignature = new Signature(Signature.Type.APPROVER, "Magneto");
    report.setRnrSignatures(asList(submitterSignature, approverSignature));

    service.submitReport(report, 1L);

    assertThat(requisition.getRnrSignatures().get(0).getText(), is("Mystique"));
    assertThat(requisition.getRnrSignatures().get(1).getText(), is("Magneto"));
    assertThat(requisition.getRnrSignatures().get(0).getCreatedBy(), is(user.getId()));
    assertThat(requisition.getRnrSignatures().get(0).getModifiedBy(), is(user.getId()));
    assertThat(requisition.getRnrSignatures().get(1).getCreatedBy(), is(user.getId()));
    assertThat(requisition.getRnrSignatures().get(1).getModifiedBy(), is(user.getId()));
    verify(requisitionService).insertRnrSignatures(requisition);
  }

  private List<RnrColumn> getRnrColumns() {
    RnrColumn rnrColumn1 = createRnrColumn("stockInHand", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn2 = createRnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn3 = createRnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn4 = createRnrColumn("quantityDispensed", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn5 = createRnrColumn("totalLossesAndAdjustments", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn6 = createRnrColumn("newPatientCount", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn7 = createRnrColumn("stockOutDays", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn8 = createRnrColumn("quantityRequested", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn9 = createRnrColumn("reasonForRequestedQuantity", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn10 = createRnrColumn("remarks", true, RnRColumnSource.USER_INPUT);
    return asList(rnrColumn1, rnrColumn2, rnrColumn3, rnrColumn4, rnrColumn5, rnrColumn6, rnrColumn7, rnrColumn8, rnrColumn9, rnrColumn10);
  }

  private RnrColumn createRnrColumn(String name, boolean visibility, RnRColumnSource source) {
    RnrColumn stockInHandColumn = new RnrColumn();
    stockInHandColumn.setName(name);
    stockInHandColumn.setVisible(visibility);
    stockInHandColumn.setSource(source);
    return stockInHandColumn;
  }
}
