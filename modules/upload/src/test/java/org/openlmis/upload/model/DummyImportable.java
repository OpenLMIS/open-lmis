/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.model;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import java.util.Date;

@Data
public class DummyImportable implements Importable {

  @ImportField(mandatory = true, name = "Mandatory String Field", type = "String")
  String mandatoryStringField;

  @ImportField(mandatory = true, type = "int")
  int mandatoryIntField;

  @ImportField
  String optionalStringField;

  @ImportField(type = "int", name = "OPTIONAL INT FIELD")
  int optionalIntField;

  @ImportField(type = "Date", name = "OPTIONAL DATE FIELD")
  Date optionalDateField;

  @ImportField(type = "String", name = "OPTIONAL NESTED FIELD", nested = "code")
  DummyNestedField dummyNestedField;

  @ImportFields(importFields = {
      @ImportField(type = "String", name = "entity 1 code", nested = "entityCode1"),
      @ImportField(type = "String", name = "entity 2 code", nested = "entityCode2")})
  DummyNestedField multipleNestedFields;

  String nonAnnotatedField;

}

