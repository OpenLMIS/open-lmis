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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openlmis.distribution.domain.DistributionStatus;

import java.io.IOException;

/**
 * StatusSerializer class represents the serializer for DistributionStatus.
 */

public class StatusSerializer extends JsonSerializer<DistributionStatus> {

  @Override
  public void serialize(DistributionStatus status, JsonGenerator generator,
                        SerializerProvider provider) throws IOException {

    generator.writeStartObject();
    generator.writeFieldName("name");
    generator.writeString(status.name());
    generator.writeFieldName("statusKey");
    generator.writeString(status.getStatusKey());
    generator.writeEndObject();
  }
}

