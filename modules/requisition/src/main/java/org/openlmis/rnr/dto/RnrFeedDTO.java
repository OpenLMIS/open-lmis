/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;

import java.io.IOException;

@Data
@AllArgsConstructor
public class RnrFeedDTO {
  private Integer requisitionId;
  private Integer facilityId;
  private Integer programId;
  private Integer periodId;
  private RnrStatus requisitionStatus;

  public static RnrFeedDTO populate(Rnr rnr) {
    return new RnrFeedDTO(rnr.getId(), rnr.getFacility().getId(), rnr.getProgram().getId(), rnr.getPeriod().getId(), rnr.getStatus());
  }

  @JsonIgnore
  public String getSerializedContents() {
    ObjectMapper mapper = new ObjectMapper();
    String feed;
    try {
      feed = mapper.writeValueAsString(this);
    } catch (IOException e) {
      throw new DataException("error.serialization");
    }
    return feed;
  }
}
