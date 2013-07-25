/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.exception.DataException;

@Data
@NoArgsConstructor
public class CHW {

  private String agentCode;
  private String agentName;
  private String parentFacilityCode;
  private String phoneNumber;
  private String active;

  public void validate() {
    if (StringUtils.isEmpty(agentCode) || StringUtils.isEmpty(agentName) || StringUtils.isEmpty(parentFacilityCode)) {
      throw new DataException("error.restapi.mandatory.missing");
    }
    if (active != null && !validateActive(active)) {
      throw new DataException("error.active.invalid");
    }
    active = StringUtils.isEmpty(active) ? "true" : active;
  }

  private boolean validateActive(String active) {
    if (active.trim().equalsIgnoreCase("true") || active.trim().equalsIgnoreCase("false")) {
      this.active = active.trim();
      return true;
    }
    return false;
  }

}
