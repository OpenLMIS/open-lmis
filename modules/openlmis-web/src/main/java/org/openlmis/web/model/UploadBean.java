/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;

@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(value = JsonMethod.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class UploadBean {
  @Setter
  private String displayName;

  @Setter @Getter
  private RecordHandler recordHandler;

  @Setter @Getter
  private Class<? extends Importable> importableClass;

  @Setter @Getter
  private String tableName;

  public UploadBean(String displayName, RecordHandler handler, Class<? extends Importable> importableClass) {
    this.displayName = displayName;
    this.recordHandler = handler;
    this.importableClass = importableClass;
  }


  @JsonProperty
  @SuppressWarnings("unused")
  public String getDisplayName() {
    return displayName;
  }
}
