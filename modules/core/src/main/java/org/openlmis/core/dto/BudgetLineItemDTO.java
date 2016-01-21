/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * BudgetLineItemDTO is Data transfer object for BudgetLineItems, consolidates user provided information like
 * facilityCode, programCode etc., to be later referenced using Ids in BudgetLineItem.
 */
@Getter
@Setter
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
      throw new DataException(new OpenLmisMessage("error.mandatory.fields.missing"));
  }
}
