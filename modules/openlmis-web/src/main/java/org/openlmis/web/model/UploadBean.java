/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
