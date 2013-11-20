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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.RequisitionValidator;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.FacilityBuilder.facilityId;
import static org.openlmis.core.builder.ProgramSupportedBuilder.PROGRAM_ID;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.restapi.builder.ReportBuilder.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({RestRequisitionService.class, ReplenishmentDTO.class})
public class RestRequisitionServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionService requisitionService;
  @Mock
  UserService userService;
  @Mock
  private OrderService orderService;
  @Mock
  private FacilityService facilityService;

  @Mock
  private RnrTemplateService rnrTemplateService;

  @Mock
  private RequisitionValidator requisitionValidator;

  @InjectMocks
  RestRequisitionService service;
  Rnr requisition;
  Report report;
  User user;

  byte[] encodedCredentialsBytes;

  @Mock
  private ProgramService programService;

  @Mock
  private ProcessingScheduleService processingScheduleService;

  @Mock
  private ProductService productService;

  String validProductCode;
  RnrLineItem rnrLineItem;

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
    Facility reportingFacility = new Facility(1L);
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportingFacility);
    when(requisitionService.initiate(reportingFacility, new Program(report.getProgramId()), user.getId(), false))
        .thenReturn(requisition);
    mockStatic(Base64.class);
    encodedCredentialsBytes = encodedCredentials.getBytes();
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, "P10")));
    List<RnrLineItem> products = asList(rnrLineItem);
    requisition.setFullSupplyLineItems(products);
    requisition.setProgram(new Program());
    report.setProducts(products);

    Long facility_id = 5L;

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    Facility facility = make(a(defaultFacility, with(facilityId, facility_id), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));

    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));
    when(requisitionService.initiate(facility, new Program(PROGRAM_ID), user.getId(), false)).thenReturn(requisition);
    when(requisitionService.save(requisition)).thenReturn(requisition);
    when(productService.getByCode(validProductCode)).thenReturn(new Product());
    Rnr reportedRequisition = mock(Rnr.class);
    whenNew(Rnr.class).withArguments(requisition.getId()).thenReturn(reportedRequisition);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));

    when(requisitionService.submit(requisition)).thenReturn(requisition);

    Rnr expectedRequisition = service.submitReport(report, 1L);

    verify(facilityService).getOperativeFacilityByCode(DEFAULT_AGENT_CODE);
    verify(programService).getValidatedProgramByCode(DEFAULT_PROGRAM_CODE);
    verify(requisitionService).initiate(facility, new Program(PROGRAM_ID), 1L, false);
    assertThat(expectedRequisition, is(requisition));
  }

  @Test
  public void shouldThrowErrorIfPeriodValidationFails() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr.error");

    doThrow(new DataException("rnr.error")).when(requisitionValidator).validatePeriod(any(Facility.class), any(Program.class));

    service.submitReport(report, 1l);

    verify(requisitionService, never()).initiate(any(Facility.class), any(Program.class), any(Long.class), any(Boolean.class));
    verify(requisitionService, never()).save(any(Rnr.class));
    verify(requisitionService, never()).submit(any(Rnr.class));
  }

  @Test
  public void shouldThrowErrorIfProductValidationFails() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr.error");

    doThrow(new DataException("rnr.error")).when(requisitionValidator).validateProducts(any(Report.class), any(Rnr.class));

    service.submitReport(report, 1L);

    verify(requisitionService, never()).save(any(Rnr.class));
    verify(requisitionService, never()).submit(any(Rnr.class));
  }

  @Test
  public void shouldThrowErrorIfInvalidRequisitionIdPassed() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    requisitionFromReport.setId(1L);


    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    doThrow(new DataException("rnr not found")).when(requisitionService).getFullRequisitionById(requisitionFromReport.getId());

    expectedException.expect(DataException.class);
    expectedException.expectMessage("rnr not found");
    service.approve(spyReport, 2L);

    verify(requisitionService).getFacilityId(requisitionFromReport.getId());
  }

  @Test
  public void shouldNotApproveRnrIfDoesNotBelongToVirtualFacility() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    requisitionFromReport.setId(1L);
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, false)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(RequisitionBuilder.facility, facility)));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.approval.not.allowed");

    Report spyReport = spy(report);
    doReturn(requisitionFromReport).when(spyReport).getRequisition();

    when(requisitionService.getFullRequisitionById(requisitionFromReport.getId())).thenReturn(rnr);

    service.approve(spyReport, 2L);

    verify(requisitionService).getFullRequisitionById(requisitionFromReport.getId());
  }

  @Test
  public void shouldApproveIfRnrBelongsToVirtualFacility() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(RequisitionBuilder.facility, facility)));
    when(requisitionService.getFullRequisitionById(requisitionFromReport.getId())).thenReturn(rnr);

    service.approve(spyReport, 2L);

    verify(spyReport).getRequisition();
    verify(requisitionService).save(requisitionFromReport);
    verify(requisitionService).approve(requisitionFromReport);
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
    when(requisitionService.initiate(facility, program, 3l, false)).thenReturn(rnr);
    when(rnrTemplateService.fetchProgramTemplateForRequisition(any(Long.class))).thenReturn(new ProgramRnrTemplate(new ArrayList<RnrColumn>()));
    service.submitReport(report, 3l);

    verify(requisitionService).save(rnr);

  }

  @Test
  public void shouldThrowAnExceptionIfLineItemsCountMismatchBetweenReportAndSavedRequisition() throws Exception {
    List<RnrLineItem> productList = asList(make(a(defaultRnrLineItem, with(productCode, "P10"))));
    Report report = make(a(defaultReport, with(products, productList)));

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(RequisitionBuilder.facility, facility)));
    rnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P10"))), make(a(defaultRnrLineItem, with(productCode, "P11")))));

    when(requisitionService.getFullRequisitionById(report.getRequisitionId())).thenReturn(rnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.number.of.line.items.mismatch");

    service.approve(report, 3l);
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
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(RequisitionBuilder.facility, facility)));
    rnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P10"))), make(a(defaultRnrLineItem, with(productCode, "P11")))));
    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);
    when(requisitionService.initiate(facility, program, userId, false)).thenReturn(rnr);
    doThrow(new DataException("invalid product codes")).when(requisitionValidator).validateProducts(report, rnr);

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

    Rnr rnr = make(a(defaultRnr, with(facility, reportFacility)));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1, rnrLineItem2));

    when(requisitionService.initiate(reportFacility, program, 3l, false)).thenReturn(rnr);
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

    Rnr rnr = make(a(defaultRnr, with(facility, reportFacility), with(program, rnrProgram)));
    rnr.setFullSupplyLineItems(asList(initiatedLineItem));

    when(requisitionService.initiate(reportFacility, rnrProgram, 3l, false)).thenReturn(rnr);

    service.submitReport(report, 3l);

    assertThat(rnr.getFullSupplyLineItems().get(0).getStockInHand(), is(100));

  }

  @Test
  public void shouldSetFieldsToDefaultsIfItsNotPassedButColumnIsUserInputAndVisibleInTemplate() throws Exception {
    Program rnrProgram = new Program(PROGRAM_ID);
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

    ProgramRnrTemplate template = new ProgramRnrTemplate(asList(rnrColumn1, rnrColumn2, rnrColumn3, rnrColumn4, rnrColumn5, rnrColumn6, rnrColumn7, rnrColumn8, rnrColumn9, rnrColumn10));
    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM_ID)).thenReturn(template);

    RnrLineItem reportedLineItem = new RnrLineItem();
    reportedLineItem.setProductCode("P10");
    reportedLineItem.setStockInHand(1);
    reportedLineItem.setQuantityDispensed(1);
    RnrLineItem initiatedLineItem = make(a(defaultRnrLineItem, with(productCode, "P10"), with(stockInHand, 0)));

    report.setProducts(asList(reportedLineItem));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(rnrProgram);

    Facility reportFacility = make(a(defaultFacility, with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportFacility);

    Rnr rnr = make(a(defaultRnr, with(facility, reportFacility), with(program, rnrProgram)));
    rnr.setFullSupplyLineItems(asList(initiatedLineItem));

    when(requisitionService.initiate(reportFacility, rnrProgram, 3l, false)).thenReturn(rnr);

    service.submitReport(report, 3l);

    RnrLineItem rnrLineItem = rnr.getFullSupplyLineItems().get(0);
    assertThat(rnrLineItem.getStockInHand(), is(1));
    assertThat(rnrLineItem.getBeginningBalance(), is(0));
    assertThat(rnrLineItem.getQuantityReceived(), is(0));
    assertThat(rnrLineItem.getQuantityDispensed(), is(1));
    assertThat(rnrLineItem.getTotalLossesAndAdjustments(), is(0));
    assertThat(rnrLineItem.getNewPatientCount(), is(0));
    assertThat(rnrLineItem.getStockOutDays(), is(0));
    assertThat(rnrLineItem.getQuantityRequested(), is(0));
    assertThat(rnrLineItem.getReasonForRequestedQuantity(), is("none"));
    assertThat(rnrLineItem.getRemarks(), is("none"));
    assertThat(rnrLineItem.getSkipped(), is(false));

  }

  @Test
  public void shouldNotNotSetDefaultValueForCalculatedColumns() throws Exception {
    Program rnrProgram = new Program(PROGRAM_ID);
    RnrColumn rnrColumn1 = createRnrColumn("stockInHand", true, RnRColumnSource.CALCULATED);
    RnrColumn rnrColumn2 = createRnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn3 = createRnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn4 = createRnrColumn("quantityDispensed", true, RnRColumnSource.CALCULATED);
    RnrColumn rnrColumn5 = createRnrColumn("totalLossesAndAdjustments", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn6 = createRnrColumn("newPatientCount", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn8 = createRnrColumn("stockOutDays", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn9 = createRnrColumn("quantityRequested", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn10 = createRnrColumn("reasonForRequestedQuantity", true, RnRColumnSource.USER_INPUT);
    RnrColumn rnrColumn11 = createRnrColumn("remarks", true, RnRColumnSource.USER_INPUT);

    ProgramRnrTemplate template = new ProgramRnrTemplate(asList(rnrColumn1, rnrColumn2, rnrColumn3, rnrColumn4, rnrColumn5, rnrColumn6, rnrColumn8, rnrColumn9, rnrColumn10,rnrColumn11));


    when(rnrTemplateService.fetchProgramTemplateForRequisition(PROGRAM_ID)).thenReturn(template);

    RnrLineItem reportedLineItem = new RnrLineItem();
    reportedLineItem.setProductCode("P10");
    reportedLineItem.setBeginningBalance(100);
    RnrLineItem initiatedLineItem = make(a(defaultRnrLineItem, with(productCode, "P10"), with(stockInHand, 0)));

    report.setProducts(asList(reportedLineItem));

    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(rnrProgram);

    Facility reportFacility = make(a(defaultFacility, with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(reportFacility);

    Rnr rnr = make(a(defaultRnr, with(facility, reportFacility), with(program, rnrProgram)));
    rnr.setFullSupplyLineItems(asList(initiatedLineItem));

    when(requisitionService.initiate(reportFacility, rnrProgram, 3l, false)).thenReturn(rnr);

    service.submitReport(report, 3l);

    RnrLineItem rnrLineItem = rnr.getFullSupplyLineItems().get(0);
    assertThat(rnrLineItem.getStockInHand(), is(0));
    assertThat(rnrLineItem.getBeginningBalance(), is(100));

  }

  private RnrColumn createRnrColumn(String name, boolean visibility, RnRColumnSource source) {
    RnrColumn stockInHandColumn = new RnrColumn();
    stockInHandColumn.setName(name);
    stockInHandColumn.setVisible(visibility);
    stockInHandColumn.setSource(source);
    return stockInHandColumn;
  }
}
