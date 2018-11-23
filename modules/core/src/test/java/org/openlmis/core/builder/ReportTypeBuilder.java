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
import org.openlmis.core.domain.*;
import org.openlmis.upload.annotation.ImportField;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ReportTypeBuilder {

    public static final Long REPORT_TYPE_ID = 10l;
    public static final String REPORT_TYPE_CODE = "REPORT_TYPE_CODE_10001";

    public static final Property<ReportType, Long> reportTypeId = newProperty();
    public static final Property<ReportType, String> code = newProperty();
    public static final Property<ReportType, Long> programId = newProperty();
    public static final Property<ReportType, String> name = newProperty();
    public static final Property<ReportType, String> description = newProperty();


    public static final Instantiator<ReportType> defaultReportType = new Instantiator<ReportType>() {
        @Override
        public ReportType instantiate(PropertyLookup<ReportType> lookup) {
            ReportType rt = new ReportType();
            rt.setId(lookup.valueOf(reportTypeId, REPORT_TYPE_ID));
            rt.setCode(lookup.valueOf(code, REPORT_TYPE_CODE));
            rt.setName("rt");
            rt.setDescription("rt_desc");
            return rt;
        }
    };

}
