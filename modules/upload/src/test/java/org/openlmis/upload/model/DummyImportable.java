/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

