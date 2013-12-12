package org.openlmis.core.transformer;

import org.openlmis.core.exception.DataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LineItemTransformer {
  public Date parseDate(String dateFormat, String date) throws ParseException {
    if (dateFormat.length() != date.length()) {
      throw new DataException("wrong.data.type");
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    simpleDateFormat.setLenient(false);
    return simpleDateFormat.parse(date);
  }
}
