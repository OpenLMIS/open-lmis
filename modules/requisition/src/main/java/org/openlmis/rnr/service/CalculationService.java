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
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;

@Service
public class CalculationService {

  public static final int MILLI_SECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;

  @Autowired
  RequisitionRepository requisitionRepository;

  @Autowired
  ProcessingScheduleService processingScheduleService;

  public void perform(Rnr requisition, ProgramRnrTemplate template) {
    RnrCalculationStrategy calcStrategy = requisition.getRnrCalcStrategy();

    requisition.setFullSupplyItemsSubmittedCost(new Money("0"));
    requisition.setNonFullSupplyItemsSubmittedCost(new Money("0"));

    calculateForFullSupply(requisition, calcStrategy, template);
    calculateForNonFullSupply(requisition, calcStrategy);
  }

  public void fillReportingDays(Rnr requisition) {
    Date startDate = requisition.getPeriod().getStartDate();
    Integer M = requisition.getPeriod().getNumberOfMonths();

    List<ProcessingPeriod> twoPreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 2);

    if (twoPreviousPeriods.size() != 0) {
      M = twoPreviousPeriods.get(0).getNumberOfMonths();
      startDate = (M < 3 && twoPreviousPeriods.size() != 1) ? twoPreviousPeriods.get(1).getStartDate() :
          twoPreviousPeriods.get(0).getStartDate();
    }

    for (RnrLineItem lineItem : requisition.getNonSkippedLineItems()) {
      lineItem.setReportingDays(getReportingDaysBasedOnRequisition(requisition, lineItem.getProductCode(), startDate, M));
    }
  }

  public void fillFieldsForInitiatedRequisition(Rnr requisition, ProgramRnrTemplate rnrTemplate, RegimenTemplate regimenTemplate) {
    List<ProcessingPeriod> fivePreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 5);

    if (fivePreviousPeriods.size() == 0) {
      requisition.setFieldsAccordingToTemplateFrom(null, rnrTemplate, regimenTemplate);
      fillPreviousNCsInLineItems(requisition, requisition.getPeriod().getNumberOfMonths(), requisition.getPeriod().getStartDate());
      return;
    }

    Rnr previousRequisition = requisitionRepository.getRegularRequisitionWithLineItems(requisition.getFacility(),
        requisition.getProgram(), fivePreviousPeriods.get(0));
    requisition.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    Integer M = fivePreviousPeriods.get(0).getNumberOfMonths();
    Date trackingDate = (M == 1) ? getStartDateForNthPreviousPeriod(fivePreviousPeriods, 4) : (M == 2) ?
        getStartDateForNthPreviousPeriod(fivePreviousPeriods, 1) : fivePreviousPeriods.get(0).getStartDate();

    fillPreviousNCsInLineItems(requisition, M, trackingDate);
  }

  private Integer getReportingDaysBasedOnRequisition(Rnr requisition, String lineItemProductCode, Date startDate, Integer M) {
    if (requisition.isForVirtualFacility()) {
      Date calculationDate = requisitionRepository.getAuthorizedDateForPreviousLineItem(requisition, lineItemProductCode, startDate);
      return getDaysForNC(requisition.getCreatedDate(), calculationDate);
    } else if (requisition.isEmergency()) {
      return getDaysForNC(requisition.getCreatedDate(), requisition.getPeriod().getStartDate());
    }
    return (M * 30);
  }

  private Integer getDaysForNC(Date requisitionCreatedDate, Date calculationDate) {
    if (calculationDate != null) {
      return (int) ((requisitionCreatedDate.getTime() - calculationDate.getTime()) / MILLI_SECONDS_IN_ONE_DAY);
    }
    return null;
  }

  private void fillPreviousNCsInLineItems(Rnr requisition, Integer m, Date trackingDate) {
    if (!requisition.isForVirtualFacility())
      return;

    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      List<RnrLineItem> previousLineItems = requisitionRepository.getNRnrLineItems(lineItem.getProductCode(),
        requisition, getNumberOfPreviousNCToTrack(m), trackingDate);
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

  private Date getStartDateForNthPreviousPeriod(List<ProcessingPeriod> fivePreviousPeriods, Integer n) {
    Integer numberOfPeriods = fivePreviousPeriods.size();
    return numberOfPeriods <= n ? fivePreviousPeriods.get(numberOfPeriods - 1).getStartDate() : fivePreviousPeriods.get(n).getStartDate();
  }

  private void calculateForNonFullSupply(Rnr requisition, RnrCalculationStrategy calcStrategy) {
    for (RnrLineItem lineItem : requisition.getNonFullSupplyLineItems()) {
      lineItem.validateNonFullSupply();

      lineItem.calculatePacksToShip(calcStrategy);

      requisition.addToNonFullSupplyCost(lineItem.calculateCost());
    }
  }

  private void calculateForFullSupply(Rnr requisition, RnrCalculationStrategy calcStrategy, ProgramRnrTemplate template) {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = requisitionRepository.getLossesAndAdjustmentsTypes();

    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      if (!lineItem.getSkipped()) {

        lineItem.validateMandatoryFields(template);
        lineItem.calculateForFullSupply(calcStrategy, template, requisition.getStatus(), lossesAndAdjustmentsTypes);
        lineItem.validateCalculatedFields(template);

        requisition.addToFullSupplyCost(lineItem.calculateCost());
      }
    }
  }
}
