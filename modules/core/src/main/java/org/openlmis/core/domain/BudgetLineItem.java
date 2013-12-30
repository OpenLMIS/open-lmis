package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BudgetLineItem extends BaseModel {

  private Long facilityId;

  private Long programId;

  private Long periodId;

  private Long budgetFileId;

  private Date periodDate;

  private BigDecimal allocatedBudget;

  private String notes;

}
