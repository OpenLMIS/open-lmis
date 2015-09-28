/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {
    private static final DateTimeFormatter formatter = ISODateTimeFormat.basicDateTimeNoMillis();

    @Override
    public DateTime deserialize(JsonParser jsonparser,
                                DeserializationContext deserializationcontext) throws IOException {

        String dtToParse = jsonparser.getText();
        if (isBlank(dtToParse)) return null;
        return formatter.parseLocalDateTime(jsonparser.getText()).toDateTime();
    }
}
