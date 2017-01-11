package org.openlmis.distribution.dto;

import com.google.common.base.Optional;
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
    String name = Optional.fromNullable(this.name).or(Reading.EMPTY).getEffectiveValue();
    String title = Optional.fromNullable(this.title).or(Reading.EMPTY).getEffectiveValue();

    return new Facilitator(name, title);
  }

}
