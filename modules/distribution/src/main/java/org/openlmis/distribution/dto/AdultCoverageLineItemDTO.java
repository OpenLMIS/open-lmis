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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.AdultCoverageLineItem;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DTO for AdultCoverageLineItem. It contains the client side representation of AdultCoverageLineItem attributes.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class AdultCoverageLineItemDTO extends BaseModel {

  private Reading healthCenterTetanus1;
  private Reading outreachTetanus1;
  private Reading healthCenterTetanus2To5;
  private Reading outreachTetanus2To5;


  public AdultCoverageLineItem transform() {
    AdultCoverageLineItem adultCoverageLineItem = new AdultCoverageLineItem();
    adultCoverageLineItem.setId(this.id);
    adultCoverageLineItem.setHealthCenterTetanus1(this.healthCenterTetanus1.parsePositiveInt());
    adultCoverageLineItem.setOutreachTetanus1(this.outreachTetanus1.parsePositiveInt());
    adultCoverageLineItem.setHealthCenterTetanus2To5(this.healthCenterTetanus2To5.parsePositiveInt());
    adultCoverageLineItem.setOutreachTetanus2To5(this.outreachTetanus2To5.parsePositiveInt());
    adultCoverageLineItem.setModifiedBy(this.modifiedBy);
    return adultCoverageLineItem;
  }
}
