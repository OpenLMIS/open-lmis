package org.openlmis.restapi.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DateTimeDeserializer extends JsonDeserializer<Date> {
  @Override
  public Date deserialize(JsonParser jsonparser,
                          DeserializationContext deserializationcontext) throws IOException {
    try {
      if(isBlank(jsonparser.getText())) return null;
      return new Date(Long.parseLong(jsonparser.getText()));
    } catch (NumberFormatException e) {
      try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.parse(jsonparser.getText());
      } catch (ParseException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}

