/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramBuilder {

  public static final String PROGRAM_CODE = "YELL_FVR";
  public static final Long PROGRAM_ID = 1L;
  public static final String PROGRAM_NAME = "Yellow Fever";

  public static final Property<Program, String> programCode = newProperty();
  public static final Property<Program, String> programName = newProperty();
  public static final Property<Program, Boolean> programStatus = newProperty();
  public static final Property<Program, Boolean> templateStatus = newProperty();
  public static final Property<Program, Boolean> regimenTemplateConfigured = newProperty();
  public static final Property<Program, Boolean> push = newProperty();
  public static Property<Program, Long> programId = newProperty();

  public static final Instantiator<Program> defaultProgram = new Instantiator<Program>() {
    @Override
    public Program instantiate(PropertyLookup<Program> lookup) {
      Program program = new Program();
      program.setId(lookup.valueOf(programId, PROGRAM_ID));
      program.setName(lookup.valueOf(programName, PROGRAM_NAME));
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
      program.setDescription("Yellow Fever program");
      program.setActive(lookup.valueOf(programStatus, true));
      program.setTemplateConfigured(lookup.valueOf(templateStatus, false));
      program.setPush(lookup.valueOf(push, false));
      program.setRegimenTemplateConfigured(lookup.valueOf(regimenTemplateConfigured, Boolean.FALSE));
      return program;
    }
  };

}
