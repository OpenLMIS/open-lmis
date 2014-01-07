package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DistributionRefrigerators extends BaseModel {

  private Long facilityId;
  private Long distributionId;
  private List<RefrigeratorReading> readings;

  public DistributionRefrigerators(Facility facility, Long distributionId, List<RefrigeratorReading> readings) {
    this(facility.getId(), distributionId, readings);
  }

}
