/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Program extends BaseModel {

  private String code;
  private String name;
  private String description;
  private Boolean active;
  private boolean templateConfigured;

  public Program(Long id) {
    this.id = id;
  }

  public Program(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Program(Long id, String code, String name, String description, Boolean active, boolean templateConfigured) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.active = active;
    this.templateConfigured = templateConfigured;
    this.id = id;
  }

  public Program basicInformation() {
    return new Program(id, name);
  }

}
