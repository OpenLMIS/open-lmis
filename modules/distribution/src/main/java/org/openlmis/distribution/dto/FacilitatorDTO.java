package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.distribution.domain.Facilitator;

/**
 *  Facilitator entity represents the name and role of a person.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilitatorDTO {

  private Reading name;
  private Reading title;

  public Facilitator transform() {
    String name = Reading.safeRead(this.name).getEffectiveValue();
    String title = Reading.safeRead(this.title).getEffectiveValue();

    return new Facilitator(name, title);
  }

}
