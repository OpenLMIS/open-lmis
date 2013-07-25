/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Getter
@Setter
@NoArgsConstructor
public class SupervisoryNode extends BaseModel implements Importable {

  @ImportField(name = "Supervisory Node Code", mandatory = true)
  private String code;

  @ImportField(name = "Name of Node", mandatory = true)
  private String name;

  @ImportField(name = "Description")
  private String description;

  @ImportField(name = "Parent Node", nested = "code")
  private SupervisoryNode parent;

  @ImportField(name = "Facility Code", mandatory = true, nested = "code")
  private Facility facility;

  public Integer supervisorCount;

  public SupervisoryNode(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SupervisoryNode that = (SupervisoryNode) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
