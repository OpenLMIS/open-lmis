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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.Facility;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  ChildCoverageLineItem represents data captured against a vaccination to determine coverage for a particular
 *  category. It extends CoverageLineItem to inherit the facilityVisitId and targetGroup for the corresponding
 *  vaccination.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ChildCoverageLineItem extends CoverageLineItem {

  private String vaccination;
  private Integer healthCenter11Months;
  private Integer outreach11Months;
  private Integer healthCenter23Months;
  private Integer outreach23Months;

  public ChildCoverageLineItem(FacilityVisit facilityVisit, Facility facility, TargetGroupProduct targetGroupProduct,
                               String vaccination, Integer processingPeriodMonths) {
    super(facilityVisit, facility, targetGroupProduct, processingPeriodMonths);
    this.vaccination = vaccination;
  }
}
