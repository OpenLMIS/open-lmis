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
import org.openlmis.distribution.dto.ChildCoverageDTO;
import org.openlmis.distribution.dto.ChildCoverageLineItemDTO;
import org.openlmis.distribution.dto.OpenedVialLineItemDTO;

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
    "BCG", "Polio10", "Polio20", "IPV", "Penta1", "Penta10", "PCV", "RV Rotarix", "Sarampo", "MSD"));

  //Note that the order in which vaccines are listed here is important - it implicitly defines their intended display order
  private static List<String> validVaccinationsInOrder = Collections.unmodifiableList(
          asList("BCG", "Polio (Newborn)", "Polio 1a dose", "Polio 2a dose",
                  "Polio 3a dose", "IPV", "Penta 1a dose", "Penta 2a dose", "Penta 3a dose",
                  "PCV10 1a dose", "PCV10 2a dose", "PCV10 3a dose", "RV Rotarix 1a dose",
                  "RV Rotarix 2a dose", "Sarampo 1a dose", "Sarampo 2a dose"));

  public VaccinationChildCoverage(FacilityVisit facilityVisit, Facility facility,
                                  ProcessingPeriod period, List<TargetGroupProduct> targetGroupProducts,
                                  List<ProductVial> productVials) {
    super(facilityVisit, facility, productVials, validProductVials);

    createChildCoverageLineItems(facilityVisit, facility, targetGroupProducts, period.getNumberOfMonths());
  }

  private void createChildCoverageLineItems(FacilityVisit facilityVisit, Facility facility,
                                            List<TargetGroupProduct> targetGroupProducts, Integer processingPeriodMonths)
  {
    for(int ordinalValue=0; ordinalValue < validVaccinationsInOrder.size(); ordinalValue++)
    {
      String vaccination = validVaccinationsInOrder.get(ordinalValue);
      TargetGroupProduct targetGroup = getTargetGroupForLineItem(targetGroupProducts, vaccination);
      this.childCoverageLineItems.add(new ChildCoverageLineItem(facilityVisit, facility, targetGroup, vaccination, ordinalValue, processingPeriodMonths));
    }
  }

  public ChildCoverageDTO transform() {
    ChildCoverageDTO dto = new ChildCoverageDTO();

    List<ChildCoverageLineItemDTO> childCoverageLineItems = new ArrayList<>();
    for (ChildCoverageLineItem childCoverageLineItem : this.childCoverageLineItems) {
      childCoverageLineItems.add(childCoverageLineItem.transform());
    }

    List<OpenedVialLineItemDTO> openedVialLineItems = new ArrayList<>();
    for (OpenedVialLineItem openedVialLineItem : this.openedVialLineItems) {
      openedVialLineItems.add(openedVialLineItem.transform());
    }

    dto.setChildCoverageLineItems(childCoverageLineItems);
    dto.setOpenedVialLineItems(openedVialLineItems);

    return dto;
  }
}
