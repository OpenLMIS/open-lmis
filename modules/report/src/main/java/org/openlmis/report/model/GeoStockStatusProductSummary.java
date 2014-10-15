package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoStockStatusProductSummary{

  private Long id;

  private String code;

  private String primaryname;

  private float reported;

  private float stockedout;

  private float understocked;

  private float overstocked;

  private float adequatelystocked;
}
