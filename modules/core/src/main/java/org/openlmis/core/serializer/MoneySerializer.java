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
