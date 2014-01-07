package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DistributionRefrigerators extends BaseModel {

  private List<RefrigeratorReading> readings = new ArrayList<>();

  public DistributionRefrigerators(Long facilityVisitId, List<RefrigeratorReading> readings) {
    for (RefrigeratorReading reading : readings) {
      reading.setFacilityVisitId(facilityVisitId);
      reading.setCreatedBy(this.createdBy);
      reading.setModifiedBy(this.modifiedBy);
      this.readings.add(reading);
    }
  }
}
