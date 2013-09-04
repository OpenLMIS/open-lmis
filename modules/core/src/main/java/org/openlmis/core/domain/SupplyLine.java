/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SupplyLine extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Supervising Node", nested = "code")
  private SupervisoryNode supervisoryNode;

  @ImportField(name = "Description")
  private String description;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Facility", nested = "code")
  private Facility supplyingFacility;

  @ImportField(mandatory = true, type = "boolean", name = "Export Orders")
  private Boolean exportOrders;

  public SupplyLine(Long id){
    this.id = id;
  }
}
