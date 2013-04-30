/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicZone extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Geographic Zone Code")
  private String code;

  @ImportField(mandatory = true, name = "Geographic Zone Name")
  private String name;

  @ImportField(mandatory = true, name = "Geographic Level Code", nested = "code")
  private GeographicLevel level;

  @ImportField(name = "Geographic Zone Parent Code", nested = "code")
  private GeographicZone parent;


  public GeographicZone(Integer id, String code, String name, GeographicLevel level, GeographicZone parent) {
    this(code, name, level, parent);
    this.id = id;
  }
}
