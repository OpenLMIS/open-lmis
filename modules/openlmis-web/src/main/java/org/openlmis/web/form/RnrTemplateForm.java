/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.List;

/**
 * This entity is a container for RnrColumn list, and their source.
 */

@Data
@NoArgsConstructor
public class RnrTemplateForm {

  RnrColumnList rnrColumns;

  List<RnRColumnSource> sources;

  public RnrTemplateForm(List<RnrColumn> rnrColumns, List<RnRColumnSource> sources) {
    this.rnrColumns = new RnrColumnList();
    for (RnrColumn rnrColumn : rnrColumns) {
      this.rnrColumns.add(rnrColumn);
    }
    this.sources = sources;
  }
}