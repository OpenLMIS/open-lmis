package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicZone implements Importable {

  private Integer id;

  @ImportField(mandatory = true, name = "Geographic Zone Code")
  private String code;

  @ImportField(mandatory = true, name = "Geographic Zone Name")
  private String name;

  @ImportField(mandatory = true, name = "Geographic Level Code", nested = "code")
  private GeographicLevel level;

  @ImportField(name = "Geographic Zone Parent Code", nested = "code")
  private GeographicZone parent;

  private Integer modifiedBy;

}
