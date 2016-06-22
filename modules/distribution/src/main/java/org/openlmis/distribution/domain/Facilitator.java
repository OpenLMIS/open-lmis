package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.distribution.dto.FacilitatorDTO;
import org.openlmis.distribution.dto.Reading;

/**
 *  Facilitator entity represents the name and role of a person.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facilitator {

  private String name;
  private String title;

  public FacilitatorDTO transform() {
    return new FacilitatorDTO(new Reading(name), new Reading(title));
  }

}
