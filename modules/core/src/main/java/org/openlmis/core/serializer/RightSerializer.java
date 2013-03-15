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
    generator.writeFieldName("adminRight");
    generator.writeString(right.getAdminRight().toString());
    generator.writeEndObject();
  }
}

