/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openlmis.rnr.domain.RnRColumnSource;

import java.io.IOException;

/**
 * This class represents the serializer for rnrColumnSource.
 */

public class RnrColumnSourceSerializer extends JsonSerializer<RnRColumnSource> {

    @Override
    public void serialize(RnRColumnSource value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {

        generator.writeStartObject();
        generator.writeFieldName("description");
        generator.writeString(value.getDescription());
        generator.writeFieldName("name");
        generator.writeString(value.name());
        generator.writeFieldName("code");
        generator.writeString(value.getCode());
        generator.writeEndObject();
    }
}
