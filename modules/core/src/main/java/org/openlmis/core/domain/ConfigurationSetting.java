
/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

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
