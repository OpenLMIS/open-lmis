/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.CoverageLineItem;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DTO for ChildCoverageLineItem. It contains the client side representation of ChildCoverageLineItem attributes.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class ChildCoverageLineItemDTO extends CoverageLineItem {

  private String vaccination;
  private Integer displayOrder;
  private Reading healthCenter11Months;
  private Reading outreach11Months;
  private Reading healthCenter23Months;
  private Reading outreach23Months;

  public ChildCoverageLineItem transform() {
    Integer healthCenter11Months = Reading.safeRead(this.healthCenter11Months).parsePositiveInt();
    Integer healthCenter23Months = Reading.safeRead(this.healthCenter23Months).parsePositiveInt();
    Integer outreach11Months = Reading.safeRead(this.outreach11Months).parsePositiveInt();
    Integer outreach23Months = Reading.safeRead(this.outreach23Months).parsePositiveInt();

    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    lineItem.setHealthCenter11Months(healthCenter11Months);
    lineItem.setHealthCenter23Months(healthCenter23Months);
    lineItem.setOutreach11Months(outreach11Months);
    lineItem.setOutreach23Months(outreach23Months);
    lineItem.setId(this.id);
    lineItem.setModifiedBy(this.modifiedBy);

    return lineItem;
  }
}
