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

import static org.apache.commons.lang.StringUtils.isBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseModel {
  private String name;
  private RoleType type;
  private String description;
  private Set<Right> rights;

  public Role(String name, RoleType type, String description) {
    this(name, type, description, null);
  }

  public void validate() {
    if (isBlank(name)) throw new DataException("error.role.without.name");
    if (rights == null || rights.isEmpty())
      throw new DataException("error.role.without.rights");
  }
}
