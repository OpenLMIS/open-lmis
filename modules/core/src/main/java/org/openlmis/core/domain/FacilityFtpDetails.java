/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityFtpDetails extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Facility Code")
  private String facilityCode;

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
