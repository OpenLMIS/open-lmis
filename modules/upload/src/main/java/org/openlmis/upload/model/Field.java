/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
public class Field {
  java.lang.reflect.Field field;
  private boolean mandatory;
  private String name;
  private String nested;
  private String type;

  public Field(java.lang.reflect.Field field, ImportField annotation) {
    this.field = field;
    this.mandatory = annotation.mandatory();
    this.name = annotation.name().isEmpty() ? field.getName() : annotation.name();
    this.nested = annotation.nested();
    this.type = annotation.type();
  }

  public boolean hasName(String name) {
    return this.name.equalsIgnoreCase(name);
  }

}
