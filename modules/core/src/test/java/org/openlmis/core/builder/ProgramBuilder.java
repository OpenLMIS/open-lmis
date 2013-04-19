/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramBuilder {

  public static final String PROGRAM_CODE = "YELL_FVR";
  public static final Integer PROGRAM_ID = 1;

  public static final Property<Program, String> programCode = newProperty();
  public static final Property<Program, Boolean> programStatus = newProperty();
  public static final Property<Program, Boolean> templateStatus = newProperty();
  public static Property<Program, Integer> programId = newProperty();

  public static final Instantiator<Program> defaultProgram = new Instantiator<Program>() {
    @Override
    public Program instantiate(PropertyLookup<Program> lookup) {
      Program program = new Program();
      program.setId(lookup.valueOf(programId, 1));
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
      program.setName("Yellow Fever");
      program.setDescription("Yellow Fever program");
      program.setActive(lookup.valueOf(programStatus, true));
      program.setTemplateConfigured(lookup.valueOf(templateStatus, false));
      return program;
    }
  };

}
