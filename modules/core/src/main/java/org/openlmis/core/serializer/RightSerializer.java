/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openlmis.core.domain.Right;

import java.io.IOException;

public class RightSerializer extends JsonSerializer<Right> {


  @Override
  public void serialize(Right right, JsonGenerator generator,
                        SerializerProvider provider) throws IOException {

    generator.writeStartObject();
    generator.writeFieldName("right");
    generator.writeString(right.name());
    generator.writeFieldName("rightName");
    generator.writeString(right.getRightName());
    generator.writeFieldName("type");
    generator.writeString(right.getType().name());
    generator.writeEndObject();
  }
}

