/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Column extends BaseModel {

  protected String name;
  protected String label;
  protected boolean visible;

  public abstract Integer getColumnWidth();
}
