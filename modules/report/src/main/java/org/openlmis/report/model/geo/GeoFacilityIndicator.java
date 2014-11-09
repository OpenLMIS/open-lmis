package org.openlmis.report.model.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoFacilityIndicator {

  private Long id;

  private Long rnrid;

  private String name;

  private String mainPhone;

  private Float longitude;

  private Float latitude;

  private Boolean reported;

  private Long indicator;

  private Boolean hasContacts;

  private Boolean hasSupervisors;

}
