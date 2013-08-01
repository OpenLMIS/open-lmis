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
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L, "ARV")))));
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
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L, "ARV")))));
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
