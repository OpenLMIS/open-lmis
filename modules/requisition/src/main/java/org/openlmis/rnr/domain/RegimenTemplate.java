package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;

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

    return ((RegimenColumn) column).getVisible();
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
