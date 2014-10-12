package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoStockStatusFacilitySummary {

  private long id;

  private String name;

  private String georegion;

  private String geozone;

  private String geometry;

  private float expected;

  private float ever;

  private float total;

  private float stockedout;

  private float understocked;

  private float overstocked;

  private float adequatelystocked;

  private float stockedoutprev;

  private float understockedprev;

  private float overstockedprev;

  private float adequatelystockedprev;

  private float period;



}
