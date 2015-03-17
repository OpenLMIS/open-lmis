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
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * This class is a base class holding programId for a template associated with that program. It is used to
 * define Requisition template and Regimen templates.
 */

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
