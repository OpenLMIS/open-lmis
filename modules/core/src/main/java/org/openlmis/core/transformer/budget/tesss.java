package org.openlmis.core.transformer.budget;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class tesss {

  public static void main(String[] args) {
    String allocatedBudget = new DecimalFormat("#0.##").format(Double.valueOf("123xczzcx.45454545"));

    System.out.println(new BigDecimal(allocatedBudget));
  }
}
