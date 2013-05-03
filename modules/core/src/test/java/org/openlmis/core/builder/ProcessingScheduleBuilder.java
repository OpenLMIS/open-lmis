/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
