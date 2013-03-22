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
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

import static org.openlmis.core.service.FacilityService.SUPPORTED_PROGRAMS_INVALID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramSupported implements Importable {

  private Integer facilityId;

  @ImportField(mandatory = true, name = "Program Code", nested="code")
  private Program program;

  @ImportField(mandatory = true, name = "Facility Code")
  private String facilityCode;

  @ImportField(mandatory = true, name = "Program Is Active", type = "boolean")
  private Boolean active = false;

  @ImportField(name = "Program Start Date", type = "Date")
  private Date startDate;

  private Integer modifiedBy;
  private Date modifiedDate;

  public ProgramSupported(Integer facilityId, Program program, Boolean active, Date startDate, Date modifiedDate, Integer modifiedBy) {
    this.facilityId = facilityId;
    this.program = program;
    this.active = active;
    this.startDate = startDate;
    this.modifiedBy = modifiedBy;
    this.modifiedDate = modifiedDate;
  }

  public void isValid() {
    if (this.active && this.startDate == null)
      throw new DataException(SUPPORTED_PROGRAMS_INVALID);
  }
}
