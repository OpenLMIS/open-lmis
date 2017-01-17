package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.dto.DistributionRefrigeratorsDTO;
import org.openlmis.distribution.dto.RefrigeratorReadingDTO;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

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

  public DistributionRefrigeratorsDTO transform() {
    DistributionRefrigeratorsDTO dto = new DistributionRefrigeratorsDTO();

    List<RefrigeratorReadingDTO> readings = new ArrayList<>();
    for (RefrigeratorReading reading : this.readings) {
      readings.add(reading.transform());
    }

    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setReadings(readings);

    return dto;
  }


}
