/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RefrigeratorReadingBuilder {

  public static Property<RefrigeratorReading, String> brand = newProperty();
  public static Property<RefrigeratorReading, String> model = newProperty();
  public static Property<RefrigeratorReading, String> serialNumber = newProperty();
  public static Property<RefrigeratorReading, Float> temperature = newProperty();
  public static Property<RefrigeratorReading, String> functioningCorrectly = newProperty();
  public static Property<RefrigeratorReading, Integer> lowAlarmEvents = newProperty();
  public static Property<RefrigeratorReading, Integer> highAlarmEvents = newProperty();
  public static Property<RefrigeratorReading, String> problemSinceLastTime = newProperty();
  public static Property<RefrigeratorReading, RefrigeratorProblem> problem = newProperty();
  public static Property<RefrigeratorReading, String> notes = newProperty();
  public static Property<RefrigeratorReading, Long> modifiedBy = newProperty();
  public static Property<RefrigeratorReading, Long> createdBy = newProperty();
  public static Property<RefrigeratorReading, Date> createdDate = newProperty();


  public static final long DEFAULT_MODIFIED_BY = 1L;
  public static final long DEFAULT_CREATED_BY = 1L;
  public static final Date DEFAULT_CREATED_DATE = new Date();

  public static final Instantiator<RefrigeratorReading> defaultReading = new Instantiator<RefrigeratorReading>() {

    @Override
    public RefrigeratorReading instantiate(PropertyLookup<RefrigeratorReading> lookup) {
      RefrigeratorReading reading = new RefrigeratorReading();
      reading.setTemperature(lookup.valueOf(temperature, 32.5F));
      reading.setFunctioningCorrectly(lookup.valueOf(functioningCorrectly, "Y"));
      reading.setLowAlarmEvents(lookup.valueOf(lowAlarmEvents, 2));
      reading.setHighAlarmEvents(lookup.valueOf(highAlarmEvents, 3));
      reading.setProblemSinceLastTime(lookup.valueOf(problemSinceLastTime, "Y"));
      reading.setProblem(lookup.valueOf(problem, new RefrigeratorProblem()));
      reading.setNotes(lookup.valueOf(notes, "Notes"));
      reading.setModifiedBy(lookup.valueOf(modifiedBy, DEFAULT_MODIFIED_BY));
      reading.setCreatedBy(lookup.valueOf(createdBy, DEFAULT_CREATED_BY));
      reading.setCreatedDate(lookup.valueOf(createdDate, DEFAULT_CREATED_DATE));
      return reading;
    }
  };

}
