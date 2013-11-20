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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.calculation.DefaultStrategy;
import org.openlmis.rnr.calculation.EmergencyRnrCalcStrategy;
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.openlmis.rnr.calculation.VirtualFacilityStrategy;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

@Category(UnitTests.class)
public class CalculationServiceTest {

  private Rnr rnr;

  @Mock
  List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  @Mock
  RequisitionRepository requisitionRepository;
  @Mock
  ProcessingScheduleService processingScheduleService;

  @InjectMocks
  CalculationService calculationService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    rnr = make(a(defaultRequisition));
    when(requisitionRepository.getLossesAndAdjustmentsTypes()).thenReturn(lossesAndAdjustmentsTypes);
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

    calculationService.perform(rnr, template);

    ArgumentCaptor<DefaultStrategy> capture = forClass(DefaultStrategy.class);

    verify(firstLineItem).calculateForFullSupply(capture.capture(), eq(period), eq(template), eq(SUBMITTED), eq(lossesAndAdjustmentsTypes));
    assertThat(capture.getValue().getClass(), is(DefaultStrategy.class.getClass()));
    verify(firstLineItem).calculateCost();
    verify(secondLineItem).calculateCost();
    verify(secondLineItem).calculatePacksToShip(capture.capture());
    assertThat(capture.getValue().getClass(), is(DefaultStrategy.class.getClass()));
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("10")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("20")));
  }

  @Test
  public void shouldCalculateForEmergencyRequisitionUsingEmergencyStrategy() throws Exception {
    rnr.setEmergency(true);
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    ProgramRnrTemplate template = new ProgramRnrTemplate(Collections.<Column>emptyList());


    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    when(rnrLineItem2.calculateCost()).thenReturn(new Money("10"));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem2));

    calculationService.perform(rnr, template);

    ArgumentCaptor<EmergencyRnrCalcStrategy> captor = forClass(EmergencyRnrCalcStrategy.class);
    verify(rnrLineItem1).calculateForFullSupply(captor.capture(), eq(rnr.getPeriod()), eq(template), eq(rnr.getStatus()), eq(lossesAndAdjustmentsTypes));
    assertThat(captor.getValue().getClass(), is(EmergencyRnrCalcStrategy.class.getClass()));

    verify(rnrLineItem2).calculatePacksToShip(captor.capture());
    assertThat(captor.getValue().getClass(), is(EmergencyRnrCalcStrategy.class.getClass()));
  }

  @Test
  public void shouldCalculateForVirtualRequisitionUsingVirtualStrategy() throws Exception {
    rnr.getFacility().setVirtualFacility(true);
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    ProgramRnrTemplate template = new ProgramRnrTemplate(Collections.<Column>emptyList());


    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1));

    calculationService.perform(rnr, template);

    ArgumentCaptor<VirtualFacilityStrategy> captor = forClass(VirtualFacilityStrategy.class);
    verify(rnrLineItem1).calculateForFullSupply(captor.capture(), eq(rnr.getPeriod()), eq(template), eq(rnr.getStatus()), eq(lossesAndAdjustmentsTypes));
    verify(rnrLineItem1).validateMandatoryFields(template);
    verify(rnrLineItem1).validateCalculatedFields(template);
    assertThat(captor.getValue().getClass(), is(VirtualFacilityStrategy.class.getClass()));
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

    verify(skippedLineItem, never()).calculateForFullSupply(any(RnrCalculationStrategy.class),
        any(ProcessingPeriod.class),
        any(ProgramRnrTemplate.class),
        any(RnrStatus.class),
        anyListOf(LossesAndAdjustmentsType.class));

    verify(skippedLineItem, never()).calculateCost();
    verify(nonSkippedLineItem).calculateCost();
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("20")));
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingCurrentPeriodIfPreviousPeriodNotPresent() throws Exception {
    Date createdDateOfPreviousLineItem = setLineItemDatesAndReturnDate();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(null);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), rnr.getPeriod().getStartDate())).thenReturn(createdDateOfPreviousLineItem);

    calculationService.calculateDaysDifference(rnr);

    assertThat(lineItem.getDaysSinceLastLineItem(), is(5));
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), rnr.getPeriod().getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingPreviousPeriodIfPreviousPeriodPresentButSecondPreviousPeriodIsNotPresent() throws Exception {
    Date createdDateOfPreviousLineItem = setLineItemDatesAndReturnDate();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);
    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod));

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(null);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate())).thenReturn(createdDateOfPreviousLineItem);

    calculationService.calculateDaysDifference(rnr);

    assertThat(lineItem.getDaysSinceLastLineItem(), is(5));
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingSecondPreviousPeriodIfPreviousPeriodAndSecondPreviousPeriodPresent() throws Exception {
    Date createdDateOfPreviousLineItem = setLineItemDatesAndReturnDate();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, new Date())));
    ProcessingPeriod secondPreviousPeriod = new ProcessingPeriod();
    secondPreviousPeriod.setStartDate(new Date());

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(secondPreviousPeriod);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate())).thenReturn(createdDateOfPreviousLineItem);

    calculationService.calculateDaysDifference(rnr);

    assertThat(lineItem.getDaysSinceLastLineItem(), is(5));
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate());
  }


  @Test
  public void shouldCalculateDaysDifferenceUsingPreviousPeriodIfPreviousPeriodPresentAndNumberOfMonthsIsGreaterThanOrEqualToThree() throws Exception {
    Date createdDateOfPreviousLineItem = setLineItemDatesAndReturnDate();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);
    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.numberOfMonths, 4)));
    ProcessingPeriod secondPreviousPeriod = new ProcessingPeriod();
    secondPreviousPeriod.setStartDate(new Date());

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(secondPreviousPeriod);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate())).thenReturn(createdDateOfPreviousLineItem);

    calculationService.calculateDaysDifference(rnr);

    assertThat(lineItem.getDaysSinceLastLineItem(), is(5));
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), previousPeriod.getStartDate());
  }

  @Test
  public void shouldCalculateDaysDifferenceUsingSecondPreviousPeriodIfMIsSmallerThanThree() throws Exception {
    Date createdDateOfPreviousLineItem = setLineItemDatesAndReturnDate();

    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.numberOfMonths, 2)));
    ProcessingPeriod secondPreviousPeriod = new ProcessingPeriod();
    secondPreviousPeriod.setStartDate(new Date());

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(secondPreviousPeriod);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate())).thenReturn(createdDateOfPreviousLineItem);

    calculationService.calculateDaysDifference(rnr);

    assertThat(lineItem.getDaysSinceLastLineItem(), is(5));
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate());
  }

  @Test
  public void shouldNotCalculateDaysDifferenceIfPreviousAuthorizedLineItemIsNotPresent() throws Exception {
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);

    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.numberOfMonths, 2)));
    ProcessingPeriod secondPreviousPeriod = new ProcessingPeriod();
    secondPreviousPeriod.setStartDate(new Date());

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(secondPreviousPeriod);
    when(requisitionRepository.getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate())).thenReturn(null);

    calculationService.calculateDaysDifference(rnr);

    assertNull(lineItem.getDaysSinceLastLineItem());
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate());
  }

  @Test
  public void shouldNotCalculateDaysDifferenceIfCurrentLineItemIsSkipped() throws Exception {
    RnrLineItem lineItem = rnr.getFullSupplyLineItems().get(0);
    lineItem.setSkipped(true);
    rnr.setFullSupplyLineItems(asList(lineItem));

    ProcessingPeriod previousPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.numberOfMonths, 2)));
    ProcessingPeriod secondPreviousPeriod = new ProcessingPeriod();
    secondPreviousPeriod.setStartDate(new Date());

    when(processingScheduleService.getImmediatePreviousPeriod(rnr.getPeriod())).thenReturn(previousPeriod);
    when(processingScheduleService.getImmediatePreviousPeriod(previousPeriod)).thenReturn(secondPreviousPeriod);

    calculationService.calculateDaysDifference(rnr);

    assertNull(lineItem.getDaysSinceLastLineItem());
    verify(processingScheduleService).getImmediatePreviousPeriod(rnr.getPeriod());
    verify(processingScheduleService).getImmediatePreviousPeriod(previousPeriod);
    verify(requisitionRepository, never()).getCreatedDateForPreviousLineItem(rnr, lineItem.getProductCode(), secondPreviousPeriod.getStartDate());
  }

  private Date setLineItemDatesAndReturnDate() {
    Calendar currentDate = Calendar.getInstance();
    Calendar previousDate = (Calendar) currentDate.clone();
    previousDate.add(Calendar.DATE, -5);

    Date createdDateOfPreviousLineItem = new Date(previousDate.getTimeInMillis());
    Date createdDateOfCurrentLineItem = new Date(currentDate.getTimeInMillis());
    rnr.setCreatedDate(createdDateOfCurrentLineItem);
    return createdDateOfPreviousLineItem;
  }

}
