package org.openlmis.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.core.exception.DataException;

import java.io.IOException;

@Data
@NoArgsConstructor
public abstract class BaseFeedDTO {

  private static Logger logger = Logger.getLogger(BaseFeedDTO.class);

  @JsonIgnore
  public String getSerializedContents() {
    ObjectMapper mapper = new ObjectMapper();
    String feed;

    try {
      feed = mapper.writeValueAsString(this);
    } catch (IOException e) {
      logger.error("Error in serializing feed", e);
      throw new DataException("error.serialization");
    }
    return feed;
  }
}
