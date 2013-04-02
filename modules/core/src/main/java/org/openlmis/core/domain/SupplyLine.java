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
@EqualsAndHashCode(callSuper = true)
public class SupplyLine extends BaseModel implements Importable {

  Integer id;

  @ImportField(mandatory = true, name = "Supervising Node", nested = "code")
  SupervisoryNode supervisoryNode;

  @ImportField(name = "Description")
  String description;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  Program program;

  @ImportField(mandatory = true, name = "Facility", nested = "code")
  Facility supplyingFacility;

}
