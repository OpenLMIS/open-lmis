/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.core.service.VendorService;
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
@PrepareForTest(RestService.class)
public class RestServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionService requisitionService;
  @Mock
  UserService userService;
  @Mock
  VendorService vendorService;
  @Mock
  private OrderService orderService;

  @InjectMocks
  RestService service;
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
    when(userService.getByUsernameAndVendorId(user)).thenReturn(user);
    when(requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId()))
        .thenReturn(requisition);
    mockStatic(Base64.class);
    encodedCredentialsBytes = encodedCredentials.getBytes();
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());
    Rnr expectedRequisition = service.submitReport(report);

    verify(requisitionService).initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId());
    verify(requisitionService).submit(expectedRequisition);
    verify(requisitionService).authorize(expectedRequisition);
    assertThat(expectedRequisition, is(requisition));
    assertThat(expectedRequisition.getFullSupplyLineItems(), is(products));
  }

  @Test
  public void shouldValidateThatTheReportContainsAllMandatoryFields() throws Exception {
    Report spyReport = spy(report);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());

    service.submitReport(spyReport);

    verify(spyReport).validate();
  }

  @Test
  public void shouldValidateUserWithVendorIdAndThrowErrorIfInvalid() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());
    when(userService.getByUsernameAndVendorId(user)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.submitReport(report);
  }

  @Test
  public void shouldValidateUserWithVendorIdAndThrowErrorIfUsernameDoesNotMatchVendor() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());
    when(userService.getByUsernameAndVendorId(user)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.submitReport(report);
  }

  @Test
  public void shouldApproveAndOrderRequisition() throws Exception {
    Rnr requisitionFromReport = new Rnr();

    Report spyReport = spy(report);
    when(spyReport.getRequisition()).thenReturn(requisitionFromReport);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());

    service.approve(spyReport);

    assertThat(requisitionFromReport.getModifiedBy(), is(user.getId()));
    verify(spyReport).getRequisition();
    verify(requisitionService).save(requisitionFromReport);
    verify(requisitionService).approve(requisitionFromReport);
    verify(orderService).convertToOrder(asList(requisitionFromReport), user.getId());
  }

  @Test
  public void shouldValidateUserWithVendorIdAndThrowErrorIfUsernameDoesNotMatchVendorWhileApproving() throws Exception {
    List<RnrLineItem> products = new ArrayList<>();
    products.add(new RnrLineItem());
    report.setProducts(products);
    whenNew(User.class).withNoArguments().thenReturn(user);
    when(vendorService.getByName(report.getVendor().getName())).thenReturn(report.getVendor());
    when(userService.getByUsernameAndVendorId(user)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.username.incorrect");

    service.approve(report);
  }

}
