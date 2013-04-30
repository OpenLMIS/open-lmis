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
import org.openlmis.core.exception.DataException;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseModel {
  private String name;
  private Boolean adminRole;
  private String description;
  private Set<Right> rights;

  public Role(String name, Boolean adminRole, String description) {
    this(name, adminRole, description, null);
  }

  public Role(Integer id, String name, Boolean adminRole, String description, Set<Right> rights) {
    this(name, adminRole, description, rights);
    this.id = id;
  }

  public void validate() {
    if (name == null || name.isEmpty()) throw new DataException("Role can not be created without name.");
    if (rights == null || rights.isEmpty())
      throw new DataException("Role can not be created without any rights assigned to it.");
  }
}
