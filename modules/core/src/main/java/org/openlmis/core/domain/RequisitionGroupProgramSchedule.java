/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequisitionGroupProgramSchedule extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "RG Code", nested = "code")
  private RequisitionGroup requisitionGroup;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Schedule", nested = "code")
  private ProcessingSchedule schedule;

  @ImportField(mandatory = true, name = "Direct Delivery")
  private boolean directDelivery;

  @ImportField(name = "Drop off Facility", nested = "code")
  private Facility dropOffFacility;

  @Override
  @JsonIgnore
  public Integer getId() {
    throw new RuntimeException("Id field not supported");
  }
}
