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
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.CoverageLineItem;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DTO for AdultCoverageLineItem. It contains the client side representation of AdultCoverageLineItem attributes.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class AdultCoverageLineItemDTO extends CoverageLineItem {

  private String demographicGroup;
  private Reading healthCenterTetanus1;
  private Reading outreachTetanus1;
  private Reading healthCenterTetanus2To5;
  private Reading outreachTetanus2To5;

  public AdultCoverageLineItem transform() {
    Integer healthCenterTetanus1 = Reading.safeRead(this.healthCenterTetanus1).parsePositiveInt();
    Integer outreachTetanus1 = Reading.safeRead(this.outreachTetanus1).parsePositiveInt();
    Integer healthCenterTetanus2To5 = Reading.safeRead(this.healthCenterTetanus2To5).parsePositiveInt();
    Integer outreachTetanus2To5 = Reading.safeRead(this.outreachTetanus2To5).parsePositiveInt();

    AdultCoverageLineItem adultCoverageLineItem = new AdultCoverageLineItem();
    adultCoverageLineItem.setId(this.id);
    adultCoverageLineItem.setHealthCenterTetanus1(healthCenterTetanus1);
    adultCoverageLineItem.setOutreachTetanus1(outreachTetanus1);
    adultCoverageLineItem.setHealthCenterTetanus2To5(healthCenterTetanus2To5);
    adultCoverageLineItem.setOutreachTetanus2To5(outreachTetanus2To5);
    adultCoverageLineItem.setModifiedBy(this.modifiedBy);

    return adultCoverageLineItem;
  }
}
