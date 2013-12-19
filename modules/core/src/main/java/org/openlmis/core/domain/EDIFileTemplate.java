package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.exception.DataException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.select;

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
