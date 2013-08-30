/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openlmis.order.domain.DateFormat;

import java.io.IOException;

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