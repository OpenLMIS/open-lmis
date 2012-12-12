package org.openlmis.rnr.domain;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

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
