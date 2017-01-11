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

import com.google.common.base.Optional;
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
  private Reading healthCenter11Months;
  private Reading outreach11Months;
  private Reading healthCenter23Months;
  private Reading outreach23Months;

  public ChildCoverageLineItem transform() {
    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    lineItem.setHealthCenter11Months(getSafely(healthCenter11Months).parsePositiveInt());
    lineItem.setHealthCenter23Months(getSafely(healthCenter23Months).parsePositiveInt());
    lineItem.setOutreach11Months(getSafely(outreach11Months).parsePositiveInt());
    lineItem.setOutreach23Months(getSafely(outreach23Months).parsePositiveInt());
    lineItem.setId(this.id);
    lineItem.setModifiedBy(this.modifiedBy);

    return lineItem;
  }

  private Reading getSafely(Reading input) {
    return Optional.fromNullable(input).or(Reading.EMPTY);
  }
}
