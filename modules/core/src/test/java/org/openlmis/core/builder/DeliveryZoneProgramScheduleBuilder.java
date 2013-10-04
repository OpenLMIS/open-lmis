/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
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
