/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.exception.DataException;

/**
 * Agent represents a Facility-Like entity which can be associated with an Rnr e.g. CHW or a Virtual Facility.
 */

@Data
@NoArgsConstructor
public class Agent {

  private String agentCode;
  private String agentName;
  private String parentFacilityCode;
  private String phoneNumber;
  private String active;

  public void validate() {
    if (StringUtils.isEmpty(agentCode) || StringUtils.isEmpty(agentName) || StringUtils.isEmpty(parentFacilityCode) || StringUtils.isEmpty(active)) {
      throw new DataException("error.mandatory.fields.missing");
    }
    if (!validateActive(active)) {
      throw new DataException("error.active.invalid");
    }
  }

  private boolean validateActive(String active) {
    if (active.trim().equalsIgnoreCase("true") || active.trim().equalsIgnoreCase("false")) {
      this.active = active.trim();
      return true;
    }
    return false;
  }

}
