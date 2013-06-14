/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class DeliveryZoneProgramSchedule extends BaseModel implements Importable {

  @ImportField(name = "Program", nested = "code", mandatory = true)
  private Program program;

  @ImportField(name = "Delivery Zone", nested = "code", mandatory = true)
  private DeliveryZone deliveryZone;

  @ImportField(name = "Schedule", nested = "code", mandatory = true)
  private ProcessingSchedule schedule;

  public DeliveryZoneProgramSchedule(Long deliveryZoneId, Long programId, Long processingScheduleId) {
    deliveryZone = new DeliveryZone(deliveryZoneId);
    program = new Program(programId);
    schedule = new ProcessingSchedule(processingScheduleId);
  }
}
