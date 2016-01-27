/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright Â© 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Â 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.Â  If not, see http://www.gnu.org/licenses. Â For additional information contact info@OpenLMIS.org.Â 
 */

package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.service.PODService;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestRequisitionCalculatorTest {

  @Mock
  private RequisitionService requisitionService;

  @Mock
  private ProcessingScheduleService processingScheduleService;

  @Mock
  private PODService podService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private RestRequisitionCalculator restRequisitionCalculator;

  private List<ProcessingPeriod> emptyPeriodList;
  private List<RnrLineItem> emptyLineItemList;
  private List<OrderPODLineItem> emptyOrderPodLineItemList;

  @Before
  public void setUp() throws Exception {
    emptyPeriodList = emptyList();
    emptyLineItemList = emptyList();
    emptyOrderPodLineItemList = emptyList();
  }

  @Test
  public void shouldSkipPeriodValidationForVirtualFacility() throws Exception {
    Facility facility = new Facility();

    facility.setVirtualFacility(true);

    restRequisitionCalculator.validatePeriod(facility, new Program());
  }

  @Test
  public void shouldNotThrowErrorForIfPeriodMatchesCurrentPeriodForNonVirtualFacility() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(1L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();

    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(processingPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(processingPeriod);

    restRequisitionCalculator.validatePeriod(reportingFacility, reportingProgram);
  }

  @Test
  public void sdpShouldNotThrowErrorIfCurrentPeriodIsDifferentFromNextDefaultPeriod() throws Exception {
    ProcessingPeriod currentPeriod = new ProcessingPeriod(1L);
    ProcessingPeriod nextEligiblePeriod = new ProcessingPeriod(2L);
    ProcessingPeriod randomPeriod = new ProcessingPeriod(3L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();


    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(currentPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(nextEligiblePeriod);

    restRequisitionCalculator.validateCustomPeriod(reportingFacility, reportingProgram, randomPeriod, 1L);
  }

  @Test
  public void sdpShouldThrowErrorIfPeriodHasExistingAuthorizedRnr() throws Exception{
    ProcessingPeriod currentPeriod = new ProcessingPeriod(1L);
    ProcessingPeriod nextEligiblePeriod = new ProcessingPeriod(2L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();

    Rnr requisition = new Rnr();
    requisition.setStatus(RnrStatus.APPROVED);

    List<ProcessingPeriod> periods = new ArrayList<ProcessingPeriod>();
    periods.add(nextEligiblePeriod);

    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(currentPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(nextEligiblePeriod);

    when(requisitionService.getRequisitionsFor(any(RequisitionSearchCriteria.class), any(periods.getClass()))).thenReturn(asList(requisition));

    expectedException.expect(DataException.class);

    restRequisitionCalculator.validateCustomPeriod(reportingFacility, reportingProgram, nextEligiblePeriod, 1L);
  }

  @Test
  public void shouldThrowErrorIfCurrentPeriodIsDifferentFromNextEligiblePeriod() throws Exception {
    ProcessingPeriod currentPeriod = new ProcessingPeriod(1L);
    ProcessingPeriod nextEligiblePeriod = new ProcessingPeriod(2L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();

    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(currentPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(nextEligiblePeriod);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.previous.not.filled");

    restRequisitionCalculator.validatePeriod(reportingFacility, reportingProgram);
  }

  @Test
  public void shouldPassProductValidationIfNoneExist() throws Exception {

    Report report = new Report();
    Rnr savedRequisition = new Rnr();

    restRequisitionCalculator.validateProducts(report.getProducts(), savedRequisition);
  }

  @Test
  public void shouldPassProductValidationIfAllProductsAreValid() throws Exception {
    Report report = new Report();
    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    RnrLineItem rnrLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P12")));

    report.setProducts(asList(rnrLineItem1, rnrLineItem2));
    Rnr savedRequisition = mock(Rnr.class);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem1)).thenReturn(rnrLineItem1);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem2)).thenReturn(rnrLineItem2);

    restRequisitionCalculator.validateProducts(report.getProducts(), savedRequisition);

    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem1);
    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem2);
  }

  @Test
  public void shouldFailProductValidationIfReportedProductDoesNotBelongToRequisition() throws Exception {
    Report report = new Report();
    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    RnrLineItem rnrLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P12")));

    report.setProducts(asList(rnrLineItem1, rnrLineItem2));
    Rnr savedRequisition = mock(Rnr.class);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem1)).thenReturn(rnrLineItem1);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem2)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("code: invalid.product.codes, params: { [P12] }");

    restRequisitionCalculator.validateProducts(report.getProducts(), savedRequisition);

    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem1);
    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem2);
  }

  @Test
  public void shouldSetBeginningBalanceFromPreviousRequisitionsStockInHandGoingBack1PeriodIfMIs3() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInteger = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(beginningBalance, nullInteger)))));
    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(3);

    Date trackingDate = new Date();
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(startDate, trackingDate)));
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(asList(previousPeriod));

    when(requisitionService.getNRnrLineItems("P121", requisition, 1, trackingDate))
      .thenReturn(asList(make(a(defaultRnrLineItem, with(stockInHand, 45)))));

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(45));
  }

  @Test
  public void shouldSetBeginningBalanceFromPreviousRequisitionsStockInHandGoingBack2PeriodIfMIsLessThan3() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInteger = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(beginningBalance, nullInteger)))));
    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);

    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod));

    Date trackingDate = new Date();
    ProcessingPeriod secondPreviousPeriod = make(a(defaultProcessingPeriod, with(name, "hello"), with(startDate, trackingDate)));
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(asList(previousPeriod, secondPreviousPeriod));

    when(requisitionService.getNRnrLineItems("P121", requisition, 1, trackingDate)).thenReturn(asList(make(a(defaultRnrLineItem, with(stockInHand, 45)))));

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(45));
    verify(requisitionService).getNRnrLineItems("P121", requisition, 1, trackingDate);
  }

  @Test
  public void shouldSetBeginningBalanceFromPreviousRequisitionsStockInHandUsingCurrentPeriodIfNoPreviousPeriods() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInteger = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(beginningBalance, nullInteger)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);
    when(requisitionService.getNRnrLineItems("P121", requisition, 1, processingPeriod.getStartDate())).thenReturn(asList(make(a(defaultRnrLineItem, with(stockInHand, 45)))));

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(45));
    verify(requisitionService).getNRnrLineItems("P121", requisition, 1, processingPeriod.getStartDate());
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfNoPreviousAndCurrentStockInHandAvailable() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInteger = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(stockInHand, nullInteger), with(beginningBalance, nullInteger)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);
    when(requisitionService.getNRnrLineItems("P121", requisition, 1, processingPeriod.getStartDate())).thenReturn(emptyLineItemList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldSetBeginningBalanceToCurrentStockInHandIfAvailable() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInteger = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(stockInHand, 56), with(beginningBalance, nullInteger)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);
    when(requisitionService.getNRnrLineItems("P121", requisition, 1, processingPeriod.getStartDate())).thenReturn(emptyLineItemList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(56));
  }

  @Test
  public void shouldNotSetBeginningBalanceIfAlreadyPresent() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(beginningBalance, 56)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);
    when(requisitionService.getNRnrLineItems("P121", requisition, 1, processingPeriod.getStartDate())).thenReturn(emptyLineItemList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(56));
  }

  @Test
  public void shouldNotSetBeginningBalanceForSkippedProducts() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInt = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem,
      with(productCode, "P121"), with(stockInHand, 56), with(skipped, true), with(beginningBalance, nullInt)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getBeginningBalance(), is(nullValue()));
  }

  @Test
  public void shouldNotModifyReceivedQuantityIfPresent() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));

    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(productCode, "P121"), with(quantityReceived, 34)))));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(emptyPeriodList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getQuantityReceived(), is(34));
  }

  @Test
  public void shouldFetchReceivedQuantityValuesFromPreviousPODGoingBack1PeriodWhenMIs3() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));
    String productCode = "P121";
    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInt = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode), with(quantityReceived, nullInt)))));
    Date trackingDate = new Date();
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(startDate, trackingDate)));
    List<OrderPODLineItem> orderPodLineItems = asList(new OrderPODLineItem(1l, productCode, 100));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(3);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 2)).thenReturn(asList(previousPeriod));
    when(podService.getNPreviousOrderPodLineItems(productCode, requisition, 1, trackingDate)).thenReturn(orderPodLineItems);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getQuantityReceived(), is(100));
  }

  @Test
  public void shouldNotModifyReceivedQuantityIfReported() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));
    String productCode = "P121";
    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode), with(quantityReceived, 34)))));
    Date trackingDate = new Date();
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(startDate, trackingDate)));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(3);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 1)).thenReturn(asList(previousPeriod));

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getQuantityReceived(), is(34));
    verify(podService, never()).getNPreviousOrderPodLineItems(productCode, requisition, 1, trackingDate);
  }

  @Test
  public void shouldSetQuantityReceivedToZeroIfNoPreviousPODFoundForProduct() throws Exception {
    String productCode = "P1234";
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 3)));
    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, processingPeriod)));
    Integer nullInt = null;
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode), with(quantityReceived, nullInt)))));
    Date trackingDate = new Date();
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(startDate, trackingDate)));

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(3);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(processingPeriod, 1)).thenReturn(asList(previousPeriod));
    when(podService.getNPreviousOrderPodLineItems(productCode, requisition, 1, trackingDate)).thenReturn(emptyOrderPodLineItemList);

    Rnr filledRequisition = restRequisitionCalculator.setDefaultValues(requisition);

    assertThat(filledRequisition.getFullSupplyLineItems().get(0).getQuantityReceived(), is(0));
  }
}
