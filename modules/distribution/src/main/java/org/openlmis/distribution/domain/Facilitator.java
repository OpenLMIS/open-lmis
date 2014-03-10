package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Facilitator entity represents the name and role of a person.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facilitator {

  private String name;
  private String title;

}
