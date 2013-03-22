/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroup implements Importable, BaseModel {


  Integer id;
  @ImportField(mandatory = true, name = "RG Code")
  String code;
  @ImportField(mandatory = true, name = "Name of RG")
  String name;
  @ImportField(name = "Description")
  String description;
  @ImportField(mandatory = true, nested = "code", name = "Supervisory Node")
  SupervisoryNode supervisoryNode;

  Integer modifiedBy;
  Date modifiedDate;

}
