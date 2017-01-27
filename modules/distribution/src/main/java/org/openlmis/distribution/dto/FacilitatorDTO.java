package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.distribution.domain.Facilitator;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  Facilitator entity represents the name and role of a person.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class FacilitatorDTO {

    private Reading name;
    private Reading title;

    public Facilitator transform() {
      String name = Reading.safeRead(this.name).getEffectiveValue();
      String title = Reading.safeRead(this.title).getEffectiveValue();

      return new Facilitator(name, title);
    }

}