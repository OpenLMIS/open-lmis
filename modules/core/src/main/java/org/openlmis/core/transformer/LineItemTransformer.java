/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.transformer;

import org.openlmis.core.exception.DataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LineItemTransformer is a base class transformer.
 */
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
