package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.RegimenColumn;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegimenTemplate {

  Long programId;

  List<RegimenColumn> regimenColumns;

  public boolean isRegimenColumnVisible(final String columnName) {

    Object column = find(this.regimenColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RegimenColumn column = (RegimenColumn) o;
        return column.getName().equalsIgnoreCase(columnName);
      }
    });

    return ((RegimenColumn) column).getVisible();
  }
}
