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

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.numberOfMonths;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.builder.RequisitionBuilder.period;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

@Category(UnitTests.class)
public class CalculationServiceTest {

  private Rnr rnr;
  private Integer M;
  @Mock
  List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  ProcessingScheduleService processingScheduleService;
  @InjectMocks
  CalculationService calculationService;

  private List<ProcessingPeriod> emptyPeriodList;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    rnr = make(a(defaultRequisition));
    M = 3;
    when(requisitionRepository.getLossesAndAdjustmentsTypes()).thenReturn(lossesAndAdjustmentsTypes);
    when(processingScheduleService.findM(rnr.getPeriod())).thenReturn(M);
    emptyPeriodList = Collections.emptyList();
  }

  @Test
  public void shouldCallValidateOnEachLineItem() throws Exception {
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);

    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    when(rnrLineItem2.calculateCost()).thenReturn(new Money("10"));

    rnr.setFullSupplyLineItems(asList(rnrLineItem1));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem2));

    List<RnrColumn> programRnrColumns = new ArrayList<>();
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRnrColumns);

    calculationService.perform(rnr, template);

    verify(rnrLineItem1).validateMandatoryFields(template);
    verify(rnrLineItem1).validateCalculatedFields(template);

    verify(rnrLineItem2).validateNonFullSupply();
  }

  @Test
  public void shouldCalculateCalculatedFieldsAccordingToProgramTemplate() throws Exception {
    ArrayList<RnrColumn> programRequisitionColumns = new ArrayList<>();
    ProcessingPeriod period = new ProcessingPeriod();
    RnrLineItem firstLineItem = mock(RnrLineItem.class);
    RnrLineItem secondLineItem = mock(RnrLineItem.class);

    rnr.setFullSupplyLineItems(asList(firstLineItem));
    rnr.setNonFullSupplyLineItems(asList(secondLineItem));
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);

    when(firstLineItem.calculateCost()).thenReturn(new Money("10"));
    when(secondLineItem.calculateCost()).thenReturn(new Money("20"));
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRequisitionColumns);
    when(processingScheduleService.findM(period)).thenReturn(M);

    calculationService.perform(rnr, template);

    verify(firstLineItem).calculateForFullSupply(eq(template), eq(SUBMITTED), eq(lossesAndAdjustmentsTypes), eq(M));
    verify(firstLineItem).calculateCost();
    verify(secondLineItem).calculateCost();
    verify(secondLineItem).calculatePacksToShip();
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("10")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("20")));
  }

  @Test
  public void shouldCalculateForVirtualRequisitionUsingDefaultStrategy() throws Exception {
    rnr.getFacility().setVirtualFacility(true);
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    ProgramRnrTemplate template = new ProgramRnrTemplate(Collections.<Column>emptyList());


    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1));

    calculationService.perform(rnr, template);

    verify(rnrLineItem1).calculateForFullSupply(eq(template), eq(rnr.getStatus()), eq(lossesAndAdjustmentsTypes), eq(M));
    verify(rnrLineItem1).validateMandatoryFields(template);
    verify(rnrLineItem1).validateCalculatedFields(template);
  }

  @Test
  public void shouldAvoidCalculationForSkippedFullSupplyLineItems() throws Exception {
    ProcessingPeriod period = new ProcessingPeriod();
    RnrLineItem skippedLineItem = mock(RnrLineItem.class);
    when(skippedLineItem.getSkipped()).thenReturn(true);

    RnrLineItem nonSkippedLineItem = mock(RnrLineItem.class);

    rnr.setFullSupplyLineItems(asList(skippedLineItem, nonSkippedLineItem));
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);

    when(nonSkippedLineItem.calculateCost()).thenReturn(new Money("20"));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    calculationService.perform(rnr, template);

    verify(skippedLineItem, never()).calculateForFullSupply(any(ProgramRnrTemplate.class),
        any(RnrStatus.class),
        anyListOf(LossesAndAdjustmentsType.class), any(Integer.class));

    verify(skippedLineItem, never()).calculateCost();
    verify(nonSkippedLineItem).calculateCost();
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("20")));
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingCurrentPeriodIfPreviousPeriodNotPresent() throws Exception {
    Date authorizedDateOfPreviousLineItem = setLineItemDatesAndReturnDate();
    Rnr rnr = getVirtualFacilityRnr();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(emptyPeriodList);
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), rnr.getPeriod().getStartDate())).thenReturn(authorizedDateOfPreviousLineItem);

    calculationService.fillReportingDays(rnr);

    assertThat(lineItem.getReportingDays(), is(5));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), rnr.getPeriod().getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingPreviousPeriodIfPreviousPeriodPresentButSecondPreviousPeriodIsNotPresent() throws Exception {
    Date authorizedDateOfPreviousLineItem = setLineItemDatesAndReturnDate();
    Rnr rnr = getVirtualFacilityRnr();
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 2, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod));
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate())).thenReturn(authorizedDateOfPreviousLineItem);

    calculationService.fillReportingDays(rnr);

    assertThat(lineItem.getReportingDays(), is(5));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingSecondPreviousPeriodIfPreviousPeriodAndSecondPreviousPeriodPresent() throws Exception {
    Date authorizedDateOfPreviousLineItem = setLineItemDatesAndReturnDate();
    Rnr rnr = getVirtualFacilityRnr();
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 2, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 2, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod, secondLastPeriod));
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate())).thenReturn(authorizedDateOfPreviousLineItem);

    calculationService.fillReportingDays(rnr);

    assertThat(lineItem.getReportingDays(), is(5));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingPreviousPeriodIfPreviousPeriodPresentAndNumberOfMonthsIsGreaterThanOrEqualToThree() throws Exception {
    Date authorizedDateOfPreviousLineItem = setLineItemDatesAndReturnDate();
    Rnr rnr = getVirtualFacilityRnr();
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 4, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod));
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate())).thenReturn(authorizedDateOfPreviousLineItem);

    calculationService.fillReportingDays(rnr);

    assertThat(lineItem.getReportingDays(), is(5));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingSecondPreviousPeriodIfMIsSmallerThanThree() throws Exception {
    Date authorizedDateOfPreviousLineItem = setLineItemDatesAndReturnDate();
    Rnr rnr = getVirtualFacilityRnr();
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 2, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 2, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod, secondLastPeriod));
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate())).thenReturn(authorizedDateOfPreviousLineItem);

    calculationService.fillReportingDays(rnr);

    assertThat(lineItem.getReportingDays(), is(5));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate());
  }

  @Test
  public void shouldNotCalculateDaysDifferenceIfPreviousAuthorizedLineItemIsNotPresent() throws Exception {
    Rnr rnr = getVirtualFacilityRnr();
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 4, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 2, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod, secondLastPeriod));
    when(requisitionRepository.getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate())).thenReturn(null);

    calculationService.fillReportingDays(rnr);

    assertNull(lineItem.getReportingDays());
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate());
  }

  @Test
  public void shouldNotCalculateDaysDifferenceIfCurrentLineItemIsSkipped() throws Exception {
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);
    lineItem.setSkipped(true);
    rnr.setFullSupplyLineItems(asList(lineItem));

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 4, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 2, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod, secondLastPeriod));

    calculationService.fillReportingDays(rnr);

    assertNull(lineItem.getReportingDays());
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository, never()).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondLastPeriod.getStartDate());
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor3MonthsInCurrentPeriodIfPreviousPeriodNotExists() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));
    requisition.setPeriod(make(a(defaultProcessingPeriod, with(numberOfMonths, 3))));

    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(null, programTemplate, regimenTemplate);

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(emptyPeriodList);
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, requisition.getPeriod().getStartDate())).thenReturn(rnrLineItems);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, requisition.getPeriod().getStartDate());
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor2MonthsInCurrentPeriodIfPreviousPeriodNotExists() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));
    requisition.setPeriod(make(a(defaultProcessingPeriod, with(numberOfMonths, 2))));

    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(null, programTemplate, regimenTemplate);

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(emptyPeriodList);
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, requisition.getPeriod().getStartDate())).thenReturn(rnrLineItems);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, requisition.getPeriod().getStartDate());
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousTwoNormalizedConsumptionFor1MonthInCurrentPeriodIfPreviousPeriodDoesNotExist() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));
    requisition.setPeriod(make(a(defaultProcessingPeriod, with(numberOfMonths, 1))));

    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(null, programTemplate, regimenTemplate);

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(emptyPeriodList);
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    RnrLineItem rnrLineItem2 = new RnrLineItem();
    rnrLineItem2.setNormalizedConsumption(9);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem, rnrLineItem2);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 2, requisition.getPeriod().getStartDate())).thenReturn(rnrLineItems);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 2, requisition.getPeriod().getStartDate());
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4, 9)));
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor3MonthsInPreviousPeriod() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    Date trackingStartDate = new Date();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, trackingStartDate, new Date(), 3, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(asList(previousPeriod));
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, previousPeriod.getStartDate())).thenReturn(rnrLineItems);

    Rnr previousRnr = make(a(defaultRequisition, with(period, make(a(defaultProcessingPeriod, with(numberOfMonths, 3))))));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, previousPeriod.getStartDate());
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor2MonthsInPreviousPeriod() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    Date trackingStartDate = DateTime.now().minusMonths(2).toDate();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 2, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, trackingStartDate, new Date(), 2, "secondLastPeriod");

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(2);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(asList(previousPeriod, secondLastPeriod));
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate)).thenReturn(rnrLineItems);

    Rnr previousRnr = make(a(defaultRequisition, with(period, make(a(defaultProcessingPeriod, with(numberOfMonths, 3))))));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate);
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor2MonthsInPreviousPeriodAndTrackFromPreviousPeriodStartDateIfOnly1PreviousPeriodExists() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    Date trackingStartDate = DateTime.now().minusMonths(2).toDate();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, trackingStartDate, new Date(), 2, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(asList(previousPeriod));

    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate)).thenReturn(rnrLineItems);

    Rnr previousRnr = make(a(defaultRequisition, with(period, make(a(defaultProcessingPeriod, with(numberOfMonths, 3))))));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate);
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousOneNormalizedConsumptionFor2MonthsInPreviousPeriodAndShouldTrackFromLast2Periods() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    Date trackingStartDate = DateTime.now().minusMonths(2).toDate();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 2, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, trackingStartDate, new Date(), 2, "secondLastPeriod");
    ProcessingPeriod thirdLastPeriod = new ProcessingPeriod(4l, new Date(), new Date(), 1, "thirdLastPeriod");

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(2);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(asList(previousPeriod, secondLastPeriod, thirdLastPeriod));
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate)).thenReturn(rnrLineItems);

    Rnr previousRnr = make(a(defaultRequisition, with(period, make(a(defaultProcessingPeriod, with(numberOfMonths, 3))))));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 1, trackingStartDate);
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4)));
  }

  @Test
  public void shouldGetPreviousTwoNormalizedConsumptionsFor1MonthInPreviousPeriodAndShouldTrackFromLast5Periods() throws Exception {
    Rnr requisition = getVirtualFacilityRnr();
    String productCode = "Code1";
    requisition.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    Date trackingStartDate = DateTime.now().minusMonths(2).toDate();
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 1, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 1, "secondLastPeriod");
    ProcessingPeriod thirdLastPeriod = new ProcessingPeriod(4l, new Date(), new Date(), 1, "thirdLastPeriod");
    ProcessingPeriod fourthLastPeriod = new ProcessingPeriod(5l, new Date(), new Date(), 1, "fourthLastPeriod");
    ProcessingPeriod fifthLastPeriod = new ProcessingPeriod(6l, trackingStartDate, new Date(), 1, "fifthLastPeriod");

    when(processingScheduleService.findM(requisition.getPeriod())).thenReturn(1);
    when(processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5)).thenReturn(asList(previousPeriod, secondLastPeriod, thirdLastPeriod, fourthLastPeriod, fifthLastPeriod));
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setNormalizedConsumption(4);
    RnrLineItem rnrLineItem2 = new RnrLineItem();
    rnrLineItem2.setNormalizedConsumption(5);
    List<RnrLineItem> rnrLineItems = asList(rnrLineItem, rnrLineItem2);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 2, trackingStartDate)).thenReturn(rnrLineItems);

    Rnr previousRnr = make(a(defaultRequisition, with(period, make(a(defaultProcessingPeriod, with(numberOfMonths, 3))))));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(requisition).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(requisition, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, requisition, 2, trackingStartDate);
    assertThat(requisition.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(4, 5)));
  }

  @Test
  public void shouldNotTrackPreviousRequisitionsIfRegularRnrAndMIs3() throws Exception {
    Rnr spyRnr = spy(rnr);
    String productCode = "Code1";
    spyRnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 3, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 3, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(spyRnr.getPeriod(), 5)).thenReturn(asList(previousPeriod, secondLastPeriod));

    Rnr previousRnr = make(a(defaultRequisition, with(period, previousPeriod)));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(spyRnr.getFacility(), spyRnr.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(spyRnr).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(spyRnr, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(spyRnr.getPeriod(), 5);
    verify(requisitionRepository, never()).getAuthorizedRegularUnSkippedLineItems(anyString(), any(Rnr.class), anyInt(), any(Date.class));
    assertThat(rnr.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(EMPTY_LIST));
  }

  @Test
  public void shouldTrackPreviousRequisitionsIfRegularRnrAndMIs1() throws Exception {
    Rnr spyRnr = spy(rnr);
    String productCode = "Code1";
    spyRnr.setFullSupplyLineItems(asList(make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 1, "previousPeriod");
    ProcessingPeriod secondLastPeriod = new ProcessingPeriod(3l, new Date(), new Date(), 1, "secondLastPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(spyRnr.getPeriod(), 5)).thenReturn(asList(previousPeriod, secondLastPeriod));
    RnrLineItem previousLineItem1 = new RnrLineItem();
    previousLineItem1.setNormalizedConsumption(5);
    RnrLineItem previousLineItem2 = new RnrLineItem();
    previousLineItem2.setNormalizedConsumption(50);
    when(requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, spyRnr, 2, secondLastPeriod.getStartDate())).thenReturn(asList(previousLineItem1, previousLineItem2));

    Rnr previousRnr = make(a(defaultRequisition, with(period, previousPeriod)));
    ProgramRnrTemplate programTemplate = new ProgramRnrTemplate();
    RegimenTemplate regimenTemplate = new RegimenTemplate();

    when(requisitionRepository.getRegularRequisitionWithLineItems(spyRnr.getFacility(), spyRnr.getProgram(), previousPeriod)).thenReturn(previousRnr);
    doNothing().when(spyRnr).setFieldsAccordingToTemplateFrom(previousRnr, programTemplate, regimenTemplate);

    calculationService.fillFieldsForInitiatedRequisition(spyRnr, programTemplate, regimenTemplate);

    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(spyRnr.getPeriod(), 5);
    verify(requisitionRepository).getAuthorizedRegularUnSkippedLineItems(productCode, spyRnr, 2, secondLastPeriod.getStartDate());
    assertThat(spyRnr.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions(), is(asList(5, 50)));
  }

  @Test
  public void shouldSetDto90daysForRegularRnrWithMEqualTo3() throws Exception {
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    Integer numberOfMonths = 3;
    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), numberOfMonths, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod));

    calculationService.fillReportingDays(rnr);

    Integer expectedNumberOfDays = 90;
    assertThat(lineItem.getReportingDays(), is(expectedNumberOfDays));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository, never()).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  @Test
  public void shouldUseCurrentPeriodStartDateToCalculateDForEmergencyRnr() throws Exception {
    rnr.setEmergency(true);
    rnr.setCreatedDate(DateUtils.parseDate("01-02-12", "dd-MM-yy"));
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = new ProcessingPeriod(2l, new Date(), new Date(), 3, "previousPeriod");

    when(processingScheduleService.getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2)).thenReturn(asList(previousPeriod));

    calculationService.fillReportingDays(rnr);

    Integer expectedNumberOfDays = 31;
    assertThat(lineItem.getReportingDays(), is(expectedNumberOfDays));
    verify(processingScheduleService).getNPreviousPeriodsInDescOrder(rnr.getPeriod(), 2);
    verify(requisitionRepository, never()).getAuthorizedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  private Rnr getVirtualFacilityRnr() {
    //TODO: Inline method when calculating for regular
    Rnr spy = spy(rnr);
    spy.getFacility().setVirtualFacility(true);
    return spy;
  }

  private Date setLineItemDatesAndReturnDate() {
    Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    Calendar previousDate = (Calendar) currentDate.clone();
    previousDate.add(Calendar.DATE, -5);

    Date authorizedDateOfPreviousLineItem = new Date(previousDate.getTimeInMillis());
    Date createdDateOfCurrentLineItem = new Date(currentDate.getTimeInMillis());
    rnr.setCreatedDate(createdDateOfCurrentLineItem);
    return authorizedDateOfPreviousLineItem;
  }

}
