package org.openlmis.rnr.domain;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class LossesAndAdjustmentsTypeEnumDeSerializer extends JsonDeserializer<LossesAndAdjustmentsTypeEnum> {

    @Override
    public LossesAndAdjustmentsTypeEnum deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return LossesAndAdjustmentsTypeEnum.valueOf(node.get("name").getTextValue());
    }
}
