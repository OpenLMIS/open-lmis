package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EDIFileTemplate {
  protected EDIConfiguration configuration;
  protected List<EDIFileColumn> columns;

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
}
