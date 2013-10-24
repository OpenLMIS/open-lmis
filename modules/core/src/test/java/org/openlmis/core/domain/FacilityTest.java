/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.core.builder.ProgramSupportedBuilder.supportedProgram;

public class FacilityTest {

  @Test
  public void shouldReturnTrueIfTwoFacilityAreEqualIgnoringProgramsSupported() {
    Facility facilityWithPrograms = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L)))));
    }};
    facilityWithPrograms.setSupportedPrograms(programsForFacility);

    Facility facilityWithoutPrograms = make(a(FacilityBuilder.defaultFacility));

    assertTrue(facilityWithPrograms.equals(facilityWithoutPrograms));
  }

  @Test
  public void shouldReturnFalseIfTwoFacilityAreUnequalIgnoringProgramsSupported() {
    Facility facilityWithPrograms = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L)))));
    }};
    facilityWithPrograms.setSupportedPrograms(programsForFacility);

    Facility facilityWithoutPrograms = make(a(FacilityBuilder.defaultFacility, with(code, "F111")));

    assertFalse(facilityWithPrograms.equals(facilityWithoutPrograms));
  }

  @Test
  public void shouldReturnTrueIfTwoFacilityAreEqualIgnoringGeographicZoneParent() {
    Facility facilityWithPrograms = make(a(FacilityBuilder.defaultFacility));


    Facility facilityWithoutPrograms = make(a(FacilityBuilder.defaultFacility));
    facilityWithoutPrograms.getGeographicZone().setParent(null);

    assertTrue(facilityWithPrograms.equals(facilityWithoutPrograms));
  }

  @Test
  public void shouldCreateSetWithDifferentFacilities(){
    Facility facilityWithParentZone = make(a(defaultFacility)), facilityWithoutParentZone = make(a(defaultFacility));
    facilityWithoutParentZone.getGeographicZone().setName("Different name");
    facilityWithoutParentZone.getGeographicZone().setParent(null);

    Set<Facility> facilitiesSet = new HashSet<>();
    facilitiesSet.add(facilityWithParentZone);
    facilitiesSet.add(facilityWithoutParentZone);

    assertThat(facilitiesSet.size(), is(2));
    assertThat(facilitiesSet, hasItems(facilityWithoutParentZone, facilityWithParentZone));
  }
}
