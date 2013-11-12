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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.ReportBuilder.defaultReport;
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
  private String credentials;
  byte[] encodedCredentialsBytes;

  @Before
  public void setUp() throws Exception {
    credentials = "1:correct token";
    report = make(a(defaultReport));
    String encodedCredentials = "1:correct token";
    requisition = new Rnr();
    requisition.setId(2L);
    user = new User();
    user.setId(1L);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);
    when(requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId(), report.getEmergency()))
      .thenReturn(requisition);
    mockStatic(Base64.class);
    encodedCredentialsBytes = encodedCredentials.getBytes();
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    when(userService.getByUserName("1")).thenReturn(user);
    when(requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId(), false)).thenReturn(requisition);
    Rnr reportedRequisition = mock(Rnr.class);
    whenNew(Rnr.class).withArguments(requisition.getId()).thenReturn(reportedRequisition);
    Rnr expectedRequisition = service.submitReport(report);

    verify(requisitionService).initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId(), false);
    verify(requisitionService).submit(reportedRequisition);
    verify(requisitionService).authorize(expectedRequisition);
    verify(reportedRequisition).setStatus(requisition.getStatus());
    verify(reportedRequisition).setModifiedBy(requisition.getModifiedBy());
    verify(reportedRequisition).setFullSupplyLineItems(products);
    assertThat(expectedRequisition, is(requisition));
  }

  @Test
  public void shouldValidateThatTheReportContainsAllMandatoryFields() throws Exception {
    Report spyReport = spy(report);

    when(userService.getByUserName("1")).thenReturn(user);

    service.submitReport(spyReport);

    verify(spyReport).validate();
  }

  @Test
  public void shouldValidateUserThrowErrorIfInvalid() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    when(userService.getByUserName(user.getUserName())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.submitReport(report);
  }

  @Test
  public void shouldValidateUserAndThrowErrorIfUsernameDoesNotMatchVendor() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUserName(user.getUserName())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.submitReport(report);
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

    service.approve(spyReport);

    verify(requisitionService).getFacilityId(requisitionFromReport.getId());
  }

  @Test
  public void shouldNotApproveRnrIfDoesNotBelongToVirtualFacility() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    requisitionFromReport.setId(1L);
    Long facilityId = 2L;

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.approval.not.allowed");

    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    when(userService.getByUserName("1")).thenReturn(user);
    Mockito.when(requisitionService.getFacilityId(requisitionFromReport.getId())).thenReturn(facilityId);
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, false)));
    Mockito.when(facilityService.getById(facilityId)).thenReturn(facility);

    service.approve(spyReport);

    verify(requisitionService).getFacilityId(requisitionFromReport.getId());
    verify(facilityService).getById(facilityId);

  }

  @Test
  public void shouldApproveIfRnrBelongsToVirtualFacility() throws Exception {
    Rnr requisitionFromReport = new Rnr();
    Long facilityId = 2L;
    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    when(userService.getByUserName("1")).thenReturn(user);
    Mockito.when(requisitionService.getFacilityId(requisitionFromReport.getId())).thenReturn(facilityId);
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.virtualFacility, true)));
    Mockito.when(facilityService.getById(facilityId)).thenReturn(facility);

    service.approve(spyReport);

    assertThat(requisitionFromReport.getModifiedBy(), is(user.getId()));
    verify(spyReport).getRequisition();
    verify(requisitionService).save(requisitionFromReport);
    verify(requisitionService).approve(requisitionFromReport);
  }

  @Test
  public void shouldValidateUserAndThrowErrorIfUsernameDoesNotMatchVendorWhileApproving() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUserName(user.getUserName())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.approve(report);
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
