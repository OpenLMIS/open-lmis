/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openlmis.core.exception.DataException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.select;

/**
 * EDIFileTemplate represents base model for EDI file templates for all file exchanges in system like shipment, budget and order.
 * It defines the basic configuration for a file like headers are included or not and details of individual
 * columns of the file.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDIFileTemplate {

  protected EDIConfiguration configuration;
  protected List<EDIFileColumn> columns;

  public Collection<EDIFileColumn> filterIncludedColumns() {
    return select(this.columns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((EDIFileColumn) o).getInclude();
      }
    });
  }

  public void validateAndSetModifiedBy(Long userId, List<String> mandatoryColumnNames) {
    Set<Integer> positions = new HashSet();
    Integer includedColumnCount = 0;
    configuration.setModifiedBy(userId);

    for (EDIFileColumn ediFileColumn : columns) {
      ediFileColumn.validate();
      if (mandatoryColumnNames.contains(ediFileColumn.getName()) && !ediFileColumn.getInclude()) {
        throw new DataException("file.mandatory.columns.not.included");
      }
      if (ediFileColumn.getInclude()) {
        positions.add(ediFileColumn.getPosition());
        includedColumnCount++;
      }
      if (positions.size() != includedColumnCount) {
        throw new DataException("file.duplicate.position");
      }
      ediFileColumn.setModifiedBy(userId);
    }
  }

  @JsonIgnore
  public Integer getRowOffset() {
    return this.configuration.isHeaderInFile() ? 1 : 0;
  }

  @JsonIgnore
  public String getDateFormatForColumn(final String columnName) {
    EDIFileColumn column = (EDIFileColumn) CollectionUtils.find(this.columns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        EDIFileColumn fileColumn = (EDIFileColumn) o;
        return fileColumn.getName().equals(columnName) && fileColumn.getInclude();
      }
    });
    return column == null ? null : column.getDatePattern();
  }
}
