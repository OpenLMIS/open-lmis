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
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramSupported extends BaseModel implements Importable {

  private Long facilityId;

  @ImportField(mandatory = true, name = "Program Code", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Facility Code")
  private String facilityCode;

  @ImportField(mandatory = true, name = "Program Is Active", type = "boolean")
  private Boolean active = false;

  @ImportField(name = "Program Start Date", type = "Date")
  private Date startDate;

  public void isValid() {
    if (this.active && this.startDate == null)
      throw new DataException("supported.programs.invalid");
  }
}
