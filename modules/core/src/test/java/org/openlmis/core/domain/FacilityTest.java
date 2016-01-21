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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.core.builder.ProgramSupportedBuilder.supportedProgram;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(FacilityProgramProduct.class)
public class FacilityTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

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
  public void shouldCreateSetWithDifferentFacilities() {
    Facility facilityWithParentZone = make(a(defaultFacility)), facilityWithoutParentZone = make(a(defaultFacility));
    facilityWithoutParentZone.getGeographicZone().setName("Different name");
    facilityWithoutParentZone.getGeographicZone().setParent(null);

    Set<Facility> facilitiesSet = new HashSet<>();
    facilitiesSet.add(facilityWithParentZone);
    facilitiesSet.add(facilityWithoutParentZone);

    assertThat(facilitiesSet.size(), is(2));
    assertThat(facilitiesSet, hasItems(facilityWithoutParentZone, facilityWithParentZone));
  }

  @Test
  public void shouldReturnFalseIfVirtualFacilityInactive() throws Exception {
    Facility inactiveFacility = new Facility(3L);
    inactiveFacility.setActive(false);
    inactiveFacility.setEnabled(true);

    Facility parentFacility = make(a(FacilityBuilder.defaultFacility));

    assertFalse(inactiveFacility.isValid(parentFacility));
  }

  @Test
  public void shouldReturnFalseIfVirtualFacilityDisabled() throws Exception {
    Facility disabledFacility = new Facility(3L);
    disabledFacility.setEnabled(false);
    disabledFacility.setActive(true);

    Facility parentFacility = make(a(FacilityBuilder.defaultFacility));

    assertFalse(disabledFacility.isValid(parentFacility));
  }

  @Test
  public void shouldReturnFalseIfParentFacilityInactive() throws Exception {
    Facility inactiveParentFacility = new Facility(3L);
    inactiveParentFacility.setEnabled(false);
    inactiveParentFacility.setActive(true);

    Facility virtualFacility = make(a(FacilityBuilder.defaultFacility));

    assertFalse(virtualFacility.isValid(inactiveParentFacility));
  }

  @Test
  public void shouldReturnFalseIfParentFacilityDisabled() throws Exception {
    Facility disabledParentFacility = new Facility(3L);
    disabledParentFacility.setEnabled(false);
    disabledParentFacility.setActive(true);

    Facility virtualFacility = make(a(FacilityBuilder.defaultFacility));

    assertFalse(virtualFacility.isValid(disabledParentFacility));
  }

  @Test
  public void shouldReturnTrueIfVirtualFacilityActiveAndParentFacilityActive() throws Exception {
    Facility disabledParentFacility = new Facility(3L);
    disabledParentFacility.setEnabled(true);
    disabledParentFacility.setActive(true);

    Facility virtualFacility = make(a(FacilityBuilder.defaultFacility));

    assertTrue(virtualFacility.isValid(disabledParentFacility));
  }

  @Test
  public void shouldValidateOnlyFacilityIfParentNull() throws Exception {
    Facility facility = make(a(defaultFacility, with(active, true)));

    assertTrue(facility.isValid(null));
  }

  @Test
  public void shouldValidateOnlyFacilityAndThrowErrorBasedOnThatIfParentNull() throws Exception {
    Facility facility = make(a(defaultFacility, with(active, false)));

    assertFalse(facility.isValid(null));
  }


  @Test
  public void shouldReturnFacilityListWithActiveProgramProducts() {
    Facility facility = new Facility();
    ProgramSupported programSupported = new ProgramSupported();
    List<FacilityProgramProduct> programProducts = new ArrayList<>();
    programSupported.setProgramProducts(programProducts);
    facility.setSupportedPrograms(asList(programSupported));
    List<Facility> facilities = new ArrayList<>(asList(facility));
    mockStatic(FacilityProgramProduct.class);
    when(FacilityProgramProduct.filterActiveProducts(programProducts)).thenReturn(programProducts);

    List<Facility> facilitiesWithActiveProducts = Facility.filterForActiveProducts(facilities);

    assertThat(facilitiesWithActiveProducts.get(0).getSupportedPrograms().get(0).getProgramProducts(),
      is(programProducts));
  }

  @Test
  public void shouldReturnWhoRatioForFirstAndOnlySupportedProgram() throws Exception {
    ProgramSupported programSupported = mock(ProgramSupported.class);
    Double whoRatio = 127D;
    when(programSupported.getWhoRatioFor("BCG")).thenReturn(whoRatio);
    Facility facility = make(a(defaultFacility, with(programSupportedList, asList(programSupported))));

    assertThat(facility.getWhoRatioFor("BCG"), is(whoRatio));
  }

  @Test
  public void shouldReturnPackSizeForFirstAndOnlySupportedProgram() throws Exception {
    ProgramSupported programSupported = mock(ProgramSupported.class);
    Integer packSize = 10;
    when(programSupported.getPackSizeFor("BCG")).thenReturn(packSize);
    Facility facility = make(a(defaultFacility, with(programSupportedList, asList(programSupported))));

    assertThat(facility.getPackSizeFor("BCG"), is(packSize));
  }
}
