package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DistributionRefrigerators represents a container for list of RefrigeratorReading.
 */

@Data
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DistributionRefrigerators extends BaseModel {

  private List<RefrigeratorReading> readings = new ArrayList<>();

  public DistributionRefrigerators(FacilityVisit facilityVisit, List<RefrigeratorReading> readings) {
    for (RefrigeratorReading reading : readings) {
      reading.setFacilityVisitId(facilityVisit.getId());
      reading.setCreatedBy(facilityVisit.getCreatedBy());
      this.readings.add(reading);
    }
  }


}
