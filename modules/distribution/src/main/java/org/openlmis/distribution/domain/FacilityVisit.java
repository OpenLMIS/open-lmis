package org.openlmis.distribution.domain;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;

@Data
public class FacilityVisit extends BaseModel {

  private Long distributionId;
  private Long facilityId;
  private Facilitator confirmedBy;
  private Facilitator verifiedBy;
  private String observation;
}
