/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.processor;

import org.openlmis.upload.model.Field;
import org.openlmis.upload.model.ModelClass;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has mappings from type to cell processors used for parsing value in a cell to corresponding data type
 */

public class CsvCellProcessors {

  private static final String format = "dd/MM/yyyy";
  public static Map<String, CellProcessor> typeMappings = new HashMap<>();

  static {
    typeMappings.put("int", new ParseInt());
    typeMappings.put("long", new ParseLong());
    typeMappings.put("boolean", new ParseBool());
    typeMappings.put("double", new ParseDouble());
    typeMappings.put("intFromDouble", new ParseIntegerFromDouble());
    typeMappings.put("Date", new StrRegEx("^\\d{1,2}/\\d{1,2}/\\d{4}$", new ParseDate(format))); //second parameter for leniency
    typeMappings.put("String", new Trim());
    typeMappings.put("BigDecimal", new ParseBigDecimal());
  }

  public static List<CellProcessor> getProcessors(ModelClass modelClass, List<String> headers) {
    List<CellProcessor> processors = new ArrayList<>();
    for (String header : headers) {
      org.openlmis.upload.model.Field field = modelClass.findImportFieldWithName(header);
      CellProcessor processor = null;
      if (field != null) {
        processor = chainTypeProcessor(field);
      }
      processors.add(processor);
    }
    return processors;
  }


  private static CellProcessor chainTypeProcessor(Field field) {
    CellProcessor mappedProcessor = typeMappings.get(field.getType());
    return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
  }
}
