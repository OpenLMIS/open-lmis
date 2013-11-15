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
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.FacilityBuilder.facilityId;
import static org.openlmis.core.builder.ProgramSupportedBuilder.PROGRAM_ID;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.restapi.builder.ReportBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.productCode;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

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

  @Before
  public void setUp() throws Exception {
    report = make(a(defaultReport));
    String encodedCredentials = "1:correct token";
    requisition = new Rnr();
    requisition.setId(2L);
    user = new User();
    user.setId(1L);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);
    when(requisitionService.initiate(new Facility(report.getFacilityId()), new Program(report.getProgramId()), user.getId(), report.getEmergency()))
      .thenReturn(requisition);
    mockStatic(Base64.class);
    encodedCredentialsBytes = encodedCredentials.getBytes();
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);

    Long facility_id = 5L;

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    Facility facility = make(a(defaultFacility, with(facilityId, facility_id), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));

    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));
    when(requisitionService.initiate(facility, new Program(PROGRAM_ID), user.getId(), false)).thenReturn(requisition);
    when(requisitionService.save(requisition)).thenReturn(requisition);

    Rnr reportedRequisition = mock(Rnr.class);
    whenNew(Rnr.class).withArguments(requisition.getId()).thenReturn(reportedRequisition);

    Rnr expectedRequisition = service.submitReport(report, 1L);

    verify(facilityService).getOperativeFacilityByCode(DEFAULT_AGENT_CODE);
    verify(programService).getValidatedProgramByCode(DEFAULT_PROGRAM_CODE);
    verify(requisitionService).initiate(facility, new Program(PROGRAM_ID), 1L, false);
    assertThat(expectedRequisition, is(requisition));
  }

  @Test
  public void shouldThrowErrorIfCurrentPeriodIsNotTheInitiatingPeriodForNonVirtualFacility() throws Exception {
    Facility facility = make(a(defaultFacility, with(virtualFacility, false)));
    Program program = new Program(3l);
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);
    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);
    ProcessingPeriod periodValidForInitiation = new ProcessingPeriod(5l);
    when(requisitionService.getPeriodForInitiating(facility, program)).thenReturn(periodValidForInitiation);
    RequisitionSearchCriteria requisitionSearchCriteria = new RequisitionSearchCriteria();
    requisitionSearchCriteria.setFacilityId(facility.getId());
    requisitionSearchCriteria.setProgramId(program.getId());

    when(requisitionService.getCurrentPeriod(requisitionSearchCriteria)).thenReturn(new ProcessingPeriod(16l));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.previous.not.filled");

    service.submitReport(report, 1l);
  }

  @Test
  public void shouldValidateThatTheReportContainsAllMandatoryFields() throws Exception {
    Report spyReport = spy(report);

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    Facility facility = make(a(defaultFacility, with(facilityId, 5L), with(programSupportedList, asList(programSupported)), with(virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(DEFAULT_AGENT_CODE)).thenReturn(facility);
    when(programService.getValidatedProgramByCode(DEFAULT_PROGRAM_CODE)).thenReturn(new Program(PROGRAM_ID));
    when(programService.getProgramStartDate(5L, PROGRAM_ID)).thenReturn(programSupported.getStartDate());
    when(processingScheduleService.getCurrentPeriod(facility.getId(), PROGRAM_ID, programSupported.getStartDate())).thenReturn(new ProcessingPeriod(8L));

    doReturn(new Rnr()).when(spyReport).getRnrWithSkippedProducts(any(Rnr.class));
    service.submitReport(spyReport, 1L);

    verify(spyReport).validate();
  }

  @Test
  public void shouldThrowErrorIfInvalidRequisitionIdPassed() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    requisitionFromReport.setId(1L);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.requisition.id");

    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    when(userService.getByUserName("1")).thenReturn(user);
    Mockito.when(requisitionService.getFacilityId(requisitionFromReport.getId())).thenReturn(null);

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
    when(programService.getValidatedProgramByCode(report.getProgramCode())).thenReturn(program);

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    when(facilityService.getOperativeFacilityByCode(report.getAgentCode())).thenReturn(facility);

    Rnr rnr = new Rnr();
    when(requisitionService.initiate(facility, program, 3l, false)).thenReturn(rnr);

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
}
