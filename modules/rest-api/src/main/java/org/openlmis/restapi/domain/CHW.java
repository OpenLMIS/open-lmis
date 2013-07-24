/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.exception.DataException;

@Data
@NoArgsConstructor
public class CHW {

  private String agentCode;
  private String agentName;
  private String parentFacilityCode;
  private String phoneNumber;
  private Boolean active;

  public void validate() {
    if (agentCode == null || agentName == null || parentFacilityCode == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }
  }

}
