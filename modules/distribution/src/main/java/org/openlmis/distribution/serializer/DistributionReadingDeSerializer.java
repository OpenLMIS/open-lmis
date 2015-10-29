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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.openlmis.distribution.dto.Reading;

import java.io.IOException;

/**
 * DistributionReadingDeSerializer is a custom deserializer for Reading.
 */

public class DistributionReadingDeSerializer extends JsonDeserializer<Reading> {
  @Override
  public Reading deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ObjectCodec objectCodec = jp.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jp);

    JsonNode value = jsonNode.get("value");
    JsonNode notRecorded = jsonNode.get("notRecorded");

    String stringValue = value != null ? value.textValue() : null;
    Boolean notRecordedValue = notRecorded != null && notRecorded.booleanValue();

    return new Reading(stringValue, notRecordedValue);
  }
}
