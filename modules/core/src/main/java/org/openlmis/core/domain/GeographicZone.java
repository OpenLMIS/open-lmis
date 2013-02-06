package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class GeographicZone {
  Integer id;
  String name;
  GeopoliticalLevel level;
  GeographicZone parent;
}
