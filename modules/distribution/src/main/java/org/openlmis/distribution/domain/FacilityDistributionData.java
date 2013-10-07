package org.openlmis.distribution.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionData {

  private Long facilityId;
  private FacilityVisit facilityVisit;

}
