
package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.lang.String;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
public class ConfigurationSetting {

  private String key;
  private String value;
  private String name;
  private String description;
  private String valueType;
  private String valueOptions;
  private String displayOrder;
  private String groupName;
}
