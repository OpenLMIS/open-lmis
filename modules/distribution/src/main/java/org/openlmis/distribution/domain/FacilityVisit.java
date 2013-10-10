/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityVisit extends BaseModel {

  private Long distributionId;
  private Long facilityId;
  private Facilitator confirmedBy;
  private Facilitator verifiedBy;
  private String observations;

  public FacilityVisit construct(Long distributionId, Long facilityId, Long createdBy) {
    this.distributionId = distributionId;
    this.facilityId = facilityId;
    this.createdBy = createdBy;
    return this;
  }
}
