package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetLineItemDTO {

  private String facilityCode;
  private String programCode;
  private String periodStartDate;
  private String allocatedBudget;
  private String notes;

  private static Logger logger = LoggerFactory.getLogger(BudgetLineItemDTO.class);

  public static BudgetLineItemDTO populate(List<String> fieldsInOneRow, Collection<EDIFileColumn> budgetFileColumns) {
    BudgetLineItemDTO lineItemDTO = new BudgetLineItemDTO();
    for (EDIFileColumn budgetFileColumn : budgetFileColumns) {
      Integer position = budgetFileColumn.getPosition();
      String name = budgetFileColumn.getName();
      try {
        Field field = BudgetLineItemDTO.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(lineItemDTO, fieldsInOneRow.get(position - 1).trim());
      } catch (Exception e) {
        logger.error("Unable to set field '" + name +
          "' in BudgetLinetItemDTO, check mapping between DTO and BudgetFileColumn", e);
      }
    }
    return lineItemDTO;
  }

  public void checkMandatoryFields() {
    if (isBlank(this.facilityCode) || isBlank(this.programCode) || isBlank(this.allocatedBudget) || isBlank(this.periodStartDate))
      throw new DataException("mandatory.field.missing");
  }
}
