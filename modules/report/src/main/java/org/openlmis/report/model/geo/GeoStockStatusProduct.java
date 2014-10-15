package org.openlmis.report.model.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoStockStatusProduct {

  private Long rnrid;

  private Long id;

  private String name;

  private String geographiczonename;

  private String mainPhone;

  private Float longitude;

  private Float latitude;

  private Boolean stockedout;

  private Boolean understocked;

  private Boolean overstocked;

  private Boolean adequatelystocked;

  private Long indicator;

  private Boolean hasContacts;

  private String product;

  private Integer amc;

  private Integer stockinhand;

  private Double mos;


}
