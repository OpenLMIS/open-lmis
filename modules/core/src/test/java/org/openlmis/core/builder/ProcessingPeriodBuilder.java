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
import org.apache.commons.lang.time.DateUtils;
import org.openlmis.core.domain.ProcessingPeriod;

import java.text.ParseException;
import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProcessingPeriodBuilder {

  public static final Property<ProcessingPeriod, String> name = newProperty();
  public static final Property<ProcessingPeriod, String> description = newProperty();
  public static final Property<ProcessingPeriod, Long> scheduleId = newProperty();
  public static final Property<ProcessingPeriod, Date> startDate = newProperty();
  public static final Property<ProcessingPeriod, Date> endDate = newProperty();
  public static final Property<ProcessingPeriod, Integer> numberOfMonths = newProperty();
  public static final Property<ProcessingPeriod, Long> modifiedBy = newProperty();
  public static final Property<ProcessingPeriod, Long> id = newProperty();

  public static final String PERIOD_NAME = "Month1";
  public static final String PERIOD_DESC = "first month";
  public static final Long MODIFIED_BY = 1L;
  public static Date START_DATE;
  public static Date END_DATE;
  public static final Integer NUMBER_OF_MONTHS = 1;
  public static final Long SCHEDULE_ID = 1L;

  static {
    try {
      START_DATE = DateUtils.parseDate("01-01-12", new String[]{"dd-MM-yy"});
      END_DATE = DateUtils.addMonths(START_DATE, 1);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static final Instantiator<ProcessingPeriod> defaultProcessingPeriod = new Instantiator<ProcessingPeriod>() {


    @Override
    public ProcessingPeriod instantiate(PropertyLookup<ProcessingPeriod> lookup) {
      Long nullLong = null;
      ProcessingPeriod period = new ProcessingPeriod();
      period.setName(lookup.valueOf(name, PERIOD_NAME));
      period.setDescription(lookup.valueOf(description, PERIOD_DESC));
      period.setStartDate(lookup.valueOf(startDate, START_DATE));
      period.setEndDate(lookup.valueOf(endDate, END_DATE));
      period.setNumberOfMonths(lookup.valueOf(numberOfMonths, NUMBER_OF_MONTHS));
      period.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));
      period.setScheduleId(lookup.valueOf(scheduleId, SCHEDULE_ID));
      period.setId(lookup.valueOf(id, nullLong));
      return period;
    }
  };
}
