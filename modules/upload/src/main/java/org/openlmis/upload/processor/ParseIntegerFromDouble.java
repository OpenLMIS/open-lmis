/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.processor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseIntegerFromDouble extends CellProcessorAdaptor implements StringCellProcessor {

  public static final String DECIMAl_POINT = "\\.";

  public ParseIntegerFromDouble() {
    super();
  }

  public ParseIntegerFromDouble(CellProcessor next) {
    super(next);
  }

  @Override
  public Object execute(Object value, CsvContext context) {
    validateInputNotNull(value, context);

    Integer result;
    if (value instanceof Integer) {
      result = (Integer) value;
    } else if (value instanceof String) {
      String stringValue = (String) value;
      try {
        result = Integer.valueOf(stringValue);
      } catch (final NumberFormatException e) {
        result = parseIntegerPart(value, context, stringValue, e);
      }
    } else {
      final String actualClassName = value.getClass().getName();
      throw new SuperCsvCellProcessorException(String.format(
          "the input value should be of type Integer or String but is of type %s", actualClassName), context,
          this);
    }

    final Integer finalResult = result;
    return next.execute(finalResult, context);

  }

  private Integer parseIntegerPart(Object value, CsvContext context, String stringValue, NumberFormatException e) {
    Integer result;
    try {
      //Handle if value can be parsed into double
      Double.valueOf(stringValue);

      String integerPart = stringValue.split(DECIMAl_POINT)[0];

      if (integerPart.length() > 10) throw new SuperCsvCellProcessorException(
          String.format("'%s' could not be parsed as an Integer", value), context, this, e);

      Long longValue = Long.valueOf(integerPart);
      if (longValue > Integer.MAX_VALUE) throw new SuperCsvCellProcessorException(
          String.format("'%s' could not be parsed as an Integer", value), context, this, e);

      result = longValue.intValue();

    } catch (NumberFormatException e1) {
      throw new SuperCsvCellProcessorException(
          String.format("'%s' could not be parsed as an Integer", value), context, this, e);

    }
    return result;
  }
}
