/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;

/**
 * This class corresponds to Regimen Template for a program and is a container for columns in that Template.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RegimenTemplate extends Template {

  public RegimenTemplate(Long programId, List<? extends Column> listOfColumns) {
    super(programId, listOfColumns);
  }

  public boolean isRegimenColumnVisible(final String columnName) {

    Object column = find(this.columns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RegimenColumn column = (RegimenColumn) o;
        return column.getName().equalsIgnoreCase(columnName);
      }
    });

    return column != null && ((RegimenColumn) column).getVisible();
  }

  public List<? extends Column> getPrintableColumns(Boolean fullSupply) {
    List<Column> printableRegimenColumns = new ArrayList<>();

    for (Column regimenColumn : columns) {
      if (regimenColumn.getVisible()) {
        printableRegimenColumns.add(regimenColumn);
      }
    }
    return printableRegimenColumns;
  }

}
