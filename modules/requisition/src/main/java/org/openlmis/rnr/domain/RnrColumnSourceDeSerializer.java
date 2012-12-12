package org.openlmis.rnr.domain;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class RnrColumnSourceDeSerializer extends JsonDeserializer<RnRColumnSource> {

    @Override
    public RnRColumnSource deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return RnRColumnSource.valueOf(node.get("name").getTextValue());
    }
}
