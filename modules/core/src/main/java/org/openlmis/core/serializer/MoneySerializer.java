/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openlmis.core.domain.Money;

import java.io.IOException;

public class MoneySerializer extends JsonSerializer<Money> {
  @Override
  public void serialize(Money value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
    generator.writeString(value.toString());
  }
}
