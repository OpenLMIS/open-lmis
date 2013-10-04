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
import org.openlmis.core.domain.ProgramSupported;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.joda.time.DateTime.now;

public class ProgramSupportedBuilder {
  public static Property<ProgramSupported, String> supportedFacilityCode = newProperty();
  public static Property<ProgramSupported, Long> supportedFacilityId = newProperty();
  public static Property<ProgramSupported, Program> supportedProgram = newProperty();
  public static Property<ProgramSupported, Boolean> isActive = newProperty();
  public static Property<ProgramSupported, Date> startDate = newProperty();
  public static Property<ProgramSupported, Date> dateModified = newProperty();

  public static final Long FACILITY_ID = 101L;
  public static final String FACILITY_CODE = "F_CD";
  public static final Long PROGRAM_ID = 101L;
  public static final String PROGRAM_CODE = "P_CD";
  private static final String PROGRAM_NAME = "P_NAME";
  public static final Boolean IS_ACTIVE = true;
  public static final Date START_DATE = now().minusYears(5).toDate();

  public static final Instantiator<ProgramSupported> defaultProgramSupported = new Instantiator<ProgramSupported>() {
    @Override
    public ProgramSupported instantiate(PropertyLookup<ProgramSupported> lookup) {
      ProgramSupported programSupported = new ProgramSupported();
      programSupported.setFacilityCode(lookup.valueOf(supportedFacilityCode, FACILITY_CODE));
      programSupported.setFacilityId(lookup.valueOf(supportedFacilityId, FACILITY_ID));
      programSupported.setProgram(lookup.valueOf(supportedProgram, new Program(PROGRAM_ID, PROGRAM_CODE, PROGRAM_NAME, null, true, false)));
      programSupported.setStartDate(lookup.valueOf(startDate, START_DATE));
      programSupported.setActive(lookup.valueOf(isActive, IS_ACTIVE));
      programSupported.setModifiedBy(1L);
      programSupported.setModifiedDate(lookup.valueOf(dateModified, now().toDate()));
      return programSupported;
    }
  };
}
