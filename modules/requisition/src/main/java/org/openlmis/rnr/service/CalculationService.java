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

import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.collect;

/**
 * Exposes the services for calculating reference data for rnr and rnrLineItem.
 */

@Service
public class CalculationService {

  public static final int MILLI_SECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;
  public static final int MAX_NUMBER_OF_PERIODS_TO_TRACK = 5;

  @Autowired
  RequisitionRepository requisitionRepository;

  @Autowired
  ProcessingScheduleService processingScheduleService;

  public void perform(Rnr requisition, ProgramRnrTemplate template) {
    requisition.setFullSupplyItemsSubmittedCost(new Money("0"));
    requisition.setNonFullSupplyItemsSubmittedCost(new Money("0"));

    calculateForFullSupply(requisition, template);
    calculateForNonFullSupply(requisition);
  }

  public void fillReportingDays(Rnr requisition) {
    Date startDate = requisition.getPeriod().getStartDate();
    Integer numberOfMonths = requisition.getPeriod().getNumberOfMonths();

    List<ProcessingPeriod> twoPreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 2);

    if (twoPreviousPeriods.size() != 0) {
      numberOfMonths = twoPreviousPeriods.get(0).getNumberOfMonths();
      startDate = (numberOfMonths < 3 && twoPreviousPeriods.size() != 1) ? twoPreviousPeriods.get(1).getStartDate() :
        twoPreviousPeriods.get(0).getStartDate();
    }

    for (RnrLineItem lineItem : requisition.getNonSkippedLineItems()) {
      Integer reportingDays = getReportingDaysBasedOnRequisition(requisition, lineItem.getProductCode(), startDate, numberOfMonths);
      lineItem.setReportingDays(reportingDays);
    }
  }

  public void fillFieldsForInitiatedRequisition(Rnr requisition, ProgramRnrTemplate rnrTemplate, RegimenTemplate regimenTemplate) {
    List<ProcessingPeriod> fivePreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), MAX_NUMBER_OF_PERIODS_TO_TRACK);

    if (fivePreviousPeriods.size() == 0) {
      requisition.setFieldsAccordingToTemplateFrom(null, rnrTemplate, regimenTemplate);
      fillPreviousNCsInLineItems(requisition, requisition.getPeriod().getNumberOfMonths(), requisition.getPeriod().getStartDate());
      return;
    }

    Rnr previousRequisition = requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(),
      requisition.getProgram(), fivePreviousPeriods.get(0));
    requisition.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    Integer numberOfMonths = fivePreviousPeriods.get(0).getNumberOfMonths();
    Date trackingDate = fivePreviousPeriods.get(0).getStartDate();

    int lastPeriodIndex = MAX_NUMBER_OF_PERIODS_TO_TRACK - 1;
    int secondPeriodIndex = 1;

    if (numberOfMonths == 1) {
      trackingDate = getStartDateForNthPreviousPeriod(fivePreviousPeriods, lastPeriodIndex);
    } else if (numberOfMonths == 2) {
      trackingDate = getStartDateForNthPreviousPeriod(fivePreviousPeriods, secondPeriodIndex);
    }

    fillPreviousNCsInLineItems(requisition, numberOfMonths, trackingDate);
  }

  public void copySkippedFieldFromPreviousPeriod(Rnr requisition) {
    List<ProcessingPeriod> fivePreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);

    if (fivePreviousPeriods.size() == 0) {
      if(requisition.getProgram().getHideSkippedProducts()) {
        for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
          lineItem.setSkipped(true);
        }
      }
      return;
    }

    Rnr previousRequisition = requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(),
      requisition.getProgram(), fivePreviousPeriods.get(0));
    Map map = new HashMap<String, RnrLineItem>();

    if (previousRequisition != null) {
      for (RnrLineItem lineItem : previousRequisition.getFullSupplyLineItems()) {
        map.put(lineItem.getProductCode(), lineItem);
      }
      for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
        RnrLineItem previous = (RnrLineItem) map.get(lineItem.getProductCode());
        if (previous != null) {
          lineItem.setSkipped(previous.getSkipped());
        }else if(requisition.getProgram().getHideSkippedProducts()){
          lineItem.setSkipped(true);
        }
      }
    }
  }

  private Integer getReportingDaysBasedOnRequisition(Rnr requisition, String lineItemProductCode, Date startDate, Integer numberOfMonths) {
    Integer reportingDays = numberOfMonths * 30;

    if (requisition.isForVirtualFacility()) {
      Date calculationDate = requisitionRepository.getAuthorizedDateForPreviousLineItem(requisition, lineItemProductCode, startDate);
      reportingDays = getDaysForNC(requisition.getCreatedDate(), calculationDate);
    } else if (requisition.isEmergency()) {
      reportingDays = getDaysForNC(requisition.getCreatedDate(), requisition.getPeriod().getStartDate());
    }
    return reportingDays;
  }

  private Integer getDaysForNC(Date requisitionCreatedDate, Date calculationDate) {
    if (calculationDate != null) {
      return (int) ((requisitionCreatedDate.getTime() - calculationDate.getTime()) / MILLI_SECONDS_IN_ONE_DAY);
    }
    return null;
  }

  private void fillPreviousNCsInLineItems(Rnr requisition, Integer numberOfMonths, Date trackingDate) {
    if (numberOfMonths >= 3 && !(requisition.isEmergency() || requisition.isForVirtualFacility())) {
      return;
    }

    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      List<RnrLineItem> previousLineItems = requisitionRepository.getAuthorizedRegularUnSkippedLineItems(lineItem.getProductCode(),
        requisition, getNumberOfPreviousNCToTrack(numberOfMonths), trackingDate);
      List<Integer> nNormalizedConsumptions = (List<Integer>) collect(previousLineItems, new Transformer() {
        @Override
        public Object transform(Object o) {
          return ((RnrLineItem) o).getNormalizedConsumption();
        }
      });
      lineItem.setPreviousNormalizedConsumptions(nNormalizedConsumptions);
    }
  }

  private Integer getNumberOfPreviousNCToTrack(Integer m) {
    return (m == 1) ? 2 : 1;
  }

  private Date getStartDateForNthPreviousPeriod(List<ProcessingPeriod> fivePreviousPeriods, Integer index) {
    Integer numberOfPeriods = fivePreviousPeriods.size();
    return numberOfPeriods <= index ? fivePreviousPeriods.get(numberOfPeriods - 1).getStartDate() : fivePreviousPeriods.get(index).getStartDate();
  }

  private void calculateForNonFullSupply(Rnr requisition) {
    for (RnrLineItem lineItem : requisition.getNonFullSupplyLineItems()) {

      lineItem.validateNonFullSupply();

      lineItem.calculatePacksToShip();

      requisition.addToNonFullSupplyCost(lineItem.calculateCost());
    }
  }

  private void calculateForFullSupply(Rnr requisition, ProgramRnrTemplate template) {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = requisitionRepository.getLossesAndAdjustmentsTypes();
    Integer numberOfMonths = processingScheduleService.findM(requisition.getPeriod());

    for (RnrLineItem lineItem : requisition.getNonSkippedLineItems()) {

      lineItem.validateMandatoryFields(template);
      lineItem.calculateForFullSupply(template, requisition.getStatus(), lossesAndAdjustmentsTypes, numberOfMonths);
      lineItem.validateCalculatedFields(template);

      requisition.addToFullSupplyCost(lineItem.calculateCost());
    }
  }
}
