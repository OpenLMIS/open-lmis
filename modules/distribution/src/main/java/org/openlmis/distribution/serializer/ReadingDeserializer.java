/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.openlmis.distribution.dto.Reading;

import java.io.IOException;

public class ReadingDeSerializer extends JsonDeserializer<Reading> {
  @Override
  public Reading deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ObjectCodec objectCodec = jp.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jp);

    JsonNode value = jsonNode.get("value");
    JsonNode notRecorded = jsonNode.get("notRecorded");

    String stringValue = value != null ? value.getTextValue() : null;
    Boolean notRecordedValue = notRecorded != null && notRecorded.getBooleanValue();

    return new Reading(stringValue, notRecordedValue);
  }
}
