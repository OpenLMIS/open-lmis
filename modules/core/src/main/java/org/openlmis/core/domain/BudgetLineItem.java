package org.openlmis.core.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BudgetLineItem extends BaseModel {

  private String facilityCode;

  private String programCode;

  private Long periodId;

  private Long budgetFileid;

  private Date periodDate;

  private BigDecimal allocatedBudget;

  private String notes;

}
