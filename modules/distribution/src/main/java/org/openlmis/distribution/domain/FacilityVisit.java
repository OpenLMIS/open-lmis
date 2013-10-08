package org.openlmis.distribution.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityVisit extends BaseModel {

  private Long distributionId;
  private Long facilityId;
  private Facilitator confirmedBy;
  private Facilitator verifiedBy;
  private String observations;
}
