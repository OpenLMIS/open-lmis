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
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.ReportBuilder.defaultReport;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RestRequisitionService.class)
public class RestRequisitionServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionService requisitionService;
  @Mock
  UserService userService;
  @Mock
  private OrderService orderService;

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
    when(userService.getByUsername(user)).thenReturn(user);
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

    service.submitReport(spyReport);

    verify(spyReport).validate();
  }

  @Test
  public void shouldValidateUserThrowErrorIfInvalid() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    when(userService.getByUsername(user)).thenReturn(null);

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
    when(userService.getByUsername(user)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.submitReport(report);
  }

  @Test
  public void shouldApproveAndOrderRequisition() throws Exception {
    Rnr requisitionFromReport = new Rnr();

    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);

    service.approve(spyReport);

    assertThat(requisitionFromReport.getModifiedBy(), is(user.getId()));
    verify(spyReport).getRequisition();
    verify(requisitionService).save(requisitionFromReport);
    verify(requisitionService).approve(requisitionFromReport);
    verify(orderService).convertToOrder(asList(requisitionFromReport), user.getId());
  }

  @Test
  public void shouldValidateUserAndThrowErrorIfUsernameDoesNotMatchVendorWhileApproving() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(userService.getByUsername(user)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.approve(report);
  }

}
