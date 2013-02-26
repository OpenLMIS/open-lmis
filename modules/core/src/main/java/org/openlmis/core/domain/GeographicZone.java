package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class GeographicZone implements Importable {
  private Integer id;
  private String code;
  private String name;
  private GeographicLevel level;
  private GeographicZone parent;
  private String modifiedBy;
}
