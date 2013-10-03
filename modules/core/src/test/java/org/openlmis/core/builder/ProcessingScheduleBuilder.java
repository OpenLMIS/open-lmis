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
import org.openlmis.core.domain.ProcessingSchedule;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProcessingScheduleBuilder {

    public static final Property<ProcessingSchedule, Long> id = newProperty();
    public static final Property<ProcessingSchedule, String> code = newProperty();
    public static final Property<ProcessingSchedule, String> name = newProperty();
    public static final Property<ProcessingSchedule, String> description = newProperty();
    public static final Property<ProcessingSchedule, Long> modifiedBy = newProperty();

    public static final String SCHEDULE_CODE = "Q1stM";
    public static final String SCHEDULE_NAME = "QuarterMonthly";
    public static final String SCHEDULE_DESCRIPTION = "QuarterMonth";
    public static final Long SCHEDULE_MODIFIED_BY = 1L;
    public static final Long SCHEDULE_ID = 1L;
    public static final Instantiator<ProcessingSchedule> defaultProcessingSchedule = new Instantiator<ProcessingSchedule>() {

        @Override
        public ProcessingSchedule instantiate(PropertyLookup<ProcessingSchedule> lookup) {
            ProcessingSchedule processingSchedule = new ProcessingSchedule();
            processingSchedule.setCode(lookup.valueOf(code, SCHEDULE_CODE));
            processingSchedule.setName(lookup.valueOf(name, SCHEDULE_NAME));
            processingSchedule.setModifiedBy(lookup.valueOf(modifiedBy, SCHEDULE_MODIFIED_BY));
            processingSchedule.setDescription(lookup.valueOf(description, SCHEDULE_DESCRIPTION));
            processingSchedule.setId(lookup.valueOf(id, SCHEDULE_ID));
            return processingSchedule;
        }
    };
}
