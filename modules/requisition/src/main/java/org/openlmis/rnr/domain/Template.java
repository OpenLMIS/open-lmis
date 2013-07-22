/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {

  protected Long programId;

  protected List<? extends Column> columns;

  @JsonIgnore
  public List<? extends Column> getPrintableColumns(Boolean fullSupply) {
    return null;
  }

  @JsonIgnore
  public static Template getInstance(List<? extends Column> columnList) {
    Column column = columnList.get(0);
    Template template;
    if (column instanceof RegimenColumn) {
      template = new RegimenTemplate();
    } else {
      template = new ProgramRnrTemplate();
    }
    template.setColumns(columnList);
    return template;
  }

}
