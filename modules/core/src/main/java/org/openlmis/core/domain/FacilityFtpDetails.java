/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * FacilityFtpDetails represents FacilityFtpDetails entity. It is a mapping between facility and its ftp details.
 * Defines the contract for creation/upload of facility ftp details.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityFtpDetails extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Facility Code", nested = "code")
  private Facility facility;

  @ImportField(mandatory = true, name = "Host")
  private String serverHost;

  @ImportField(mandatory = true, name = "Port")
  private String serverPort;

  @ImportField(mandatory = true, name = "Username")
  private String userName;

  @ImportField(mandatory = true, name = "Password")
  private String password;

  @ImportField(mandatory = true, name = "Local Folder Path")
  private String localFolderPath;

}
