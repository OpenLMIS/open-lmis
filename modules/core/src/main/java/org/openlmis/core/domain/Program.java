/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Program implements BaseModel {

  private Integer id;
  private String code;
  private String name;
  private String description;
  private Boolean active;
  private boolean templateConfigured;

  public Program(Integer id) {
    this.id = id;
  }

  public Program(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Program basicInformation() {
    return new Program(id, name);
  }

}
