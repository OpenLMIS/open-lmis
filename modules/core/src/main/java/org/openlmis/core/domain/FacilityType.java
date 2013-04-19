/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class FacilityType {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Integer levelId;
    private Integer nominalMaxMonth;
    private Double nominalEop;
    private Integer displayOrder;
    private boolean active;

  public FacilityType(String code) {
    this.code = code;
  }

  public FacilityType(Integer id) {
    this.id = id;
  }
}
