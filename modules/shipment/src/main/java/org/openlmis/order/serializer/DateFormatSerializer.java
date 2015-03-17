/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openlmis.order.domain.DateFormat;

import java.io.IOException;

/**
 * DateFormatSerializer is a custom serializer for DateFormat.
 */

public class DateFormatSerializer extends JsonSerializer<DateFormat> {


  @Override
  public void serialize(DateFormat dateFormat, JsonGenerator generator,
                        SerializerProvider provider) throws IOException {

    generator.writeStartObject();
    generator.writeFieldName("format");
    generator.writeString(dateFormat.getFormat());
    generator.writeFieldName("orderDate");
    generator.writeBoolean(dateFormat.isOrderDate());
    generator.writeEndObject();
  }
}