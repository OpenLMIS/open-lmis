/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.distribution.domain.DeliveryZone;
import org.openlmis.distribution.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.distribution.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

public class DeliveryZoneProgramScheduleBuilder {


  public static Property<DeliveryZoneProgramSchedule,Program> program = newProperty();
  public static Property<DeliveryZoneProgramSchedule, DeliveryZone> zone = newProperty();
  public static Property<DeliveryZoneProgramSchedule,ProcessingSchedule> schedule = newProperty();


  public static final Program PROGRAM = make(a(defaultProgram));
  public static final Instantiator<DeliveryZoneProgramSchedule> defaultDZProgramSchedule = new Instantiator<DeliveryZoneProgramSchedule>() {

    @Override
    public DeliveryZoneProgramSchedule instantiate(PropertyLookup<DeliveryZoneProgramSchedule> lookup) {
      DeliveryZoneProgramSchedule deliveryZoneProgramSchedule = new DeliveryZoneProgramSchedule();
      deliveryZoneProgramSchedule.setId(1l);
      deliveryZoneProgramSchedule.setProgram(lookup.valueOf(program, PROGRAM));
      deliveryZoneProgramSchedule.setDeliveryZone(lookup.valueOf(zone, make(a(defaultDeliveryZone))));
      deliveryZoneProgramSchedule.setSchedule(lookup.valueOf(schedule, make(a(defaultProcessingSchedule))));

      deliveryZoneProgramSchedule.setModifiedBy(1l);
      deliveryZoneProgramSchedule.setCreatedBy(1l);

      return deliveryZoneProgramSchedule;
    }
  };
}
