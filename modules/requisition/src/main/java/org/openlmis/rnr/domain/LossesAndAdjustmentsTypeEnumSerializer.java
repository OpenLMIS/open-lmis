package org.openlmis.rnr.domain;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class LossesAndAdjustmentsTypeEnumSerializer extends JsonSerializer<LossesAndAdjustmentsTypeEnum> {

    @Override
    public void serialize(LossesAndAdjustmentsTypeEnum value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {

        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(value.name());
        generator.writeFieldName("description");
        generator.writeString(value.getDescription());
        generator.writeEndObject();
    }
}
