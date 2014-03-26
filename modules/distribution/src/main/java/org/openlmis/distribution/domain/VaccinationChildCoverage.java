/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *  VaccinationChildCoverage represents a container for list of ChildCoverageLineItem.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccinationChildCoverage extends VaccinationCoverage {

  private List<ChildCoverageLineItem> childCoverageLineItems = new ArrayList<>();

  private static List<String> validProductVials = Collections.unmodifiableList(asList(
    "BCG", "Polio10", "Polio20", "Penta1", "Penta10", "PCV", "Measles"));

  public VaccinationChildCoverage(FacilityVisit facilityVisit, Facility facility,
                                  ProcessingPeriod period, List<TargetGroupProduct> targetGroupProducts,
                                  List<ProductVial> productVials) {
    super(facilityVisit, facility, productVials, validProductVials);
    List<String> validVaccinations = Collections.unmodifiableList(
      asList("BCG", "Polio (Newborn)", "Polio 1st dose", "Polio 2nd dose",
        "Polio 3rd dose", "Penta 1st dose", "Penta 2nd dose", "Penta 3rd dose",
        "PCV10 1st dose", "PCV10 2nd dose", "PCV10 3rd dose", "Measles"));

    createChildCoverageLineItems(facilityVisit, facility, targetGroupProducts, validVaccinations, period.getNumberOfMonths());
  }

  private void createChildCoverageLineItems(FacilityVisit facilityVisit, Facility facility,
                                            List<TargetGroupProduct> targetGroupProducts, List<String> validVaccinations,
                                            Integer processingPeriodMonths) {
    for (String vaccination : validVaccinations) {

      TargetGroupProduct targetGroup = getTargetGroupForLineItem(targetGroupProducts, vaccination);
      this.childCoverageLineItems.add(new ChildCoverageLineItem(facilityVisit, facility, targetGroup, vaccination, processingPeriodMonths));
    }
  }
}
