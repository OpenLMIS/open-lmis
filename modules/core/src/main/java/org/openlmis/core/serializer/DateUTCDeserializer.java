/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.serializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DateUTCDeserializer extends JsonDeserializer<Long> {

    private static Calendar cal = Calendar.getInstance();
    private static int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
    private static int dstOffset = cal.get(Calendar.DST_OFFSET);

    @Override
    public Long deserialize(JsonParser jsonparser,
                            DeserializationContext deserializationcontext) throws IOException {
        try {
            if (isBlank(jsonparser.getText())) {
                return null;
            }
            cal.setTime(new Date(Long.parseLong(jsonparser.getText())));
            cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            return cal.getTime().getTime();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

