/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.programSupportedList;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest({DateTime.class})
public class ProgramSupportedRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProgramSupportedMapper programSupportedMapper;

  @InjectMocks
  private ProgramSupportedRepository programSupportedRepository;

  private DateTime now;

  @Before
  public void setUp() throws Exception {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
  }

  @Test
  public void shouldGetProgramStartDate() throws Exception {
    ProgramSupported programSupported = make(a(defaultProgramSupported));
    when(programSupportedMapper.getBy(1L, 2L)).thenReturn(programSupported);

    assertThat(programSupportedRepository.getProgramStartDate(1L, 2L), is(programSupported.getStartDate()));
  }

  @Test
  public void shouldDeleteSupportedPrograms() throws Exception {
    programSupportedRepository.deleteSupportedPrograms(1L, 2L);

    verify(programSupportedMapper).delete(1L, 2L);
  }

  @Test
  public void shouldAddProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();

    programSupportedRepository.addSupportedProgram(programSupported);

    verify(programSupportedMapper).insert(programSupported);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    Program program = new Program();
    program.setCode("program code");
    programSupported.setProgram(program);

    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(
      programSupportedMapper).insert(programSupported);

    expectedEx.expect(dataExceptionMatcher("error.facility.program.mapping.exists"));

    programSupportedRepository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldInsertIfDoestNotExist() throws Exception {

    Long facilityId = 100L;
    Long programId = 123L;
    Date date = new Date();
    ProgramSupported programSupported = new ProgramSupported(programId, false, date);
    Facility facility = make(a(defaultFacility, with(programSupportedList, asList(programSupported))));

    facility.setId(facilityId);
    List<ProgramSupported> previouslyProgramSupportedList = new ArrayList<>();
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);


    assertTrue(programSupportedRepository.updateSupportedPrograms(facility));

    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper).insert(argThat(programSupportedMatcher(facilityId, programSupported.getActive(),
      programSupported.getStartDate(), facility.getModifiedBy(), facility.getModifiedBy())));
  }

  @Test
  public void shouldDeleteIfNotSendInUpdate() throws Exception {
    Long facilityId = 100L;
    Program program = new Program(1232L);

    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedProgram, program)));
    Facility facility = make(a(defaultFacility));
    facility.setId(facilityId);

    List<ProgramSupported> previouslyProgramSupportedList = asList(programSupported);
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);

    assertTrue(programSupportedRepository.updateSupportedPrograms(facility));

    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper).delete(facilityId, program.getId());
    verify(programSupportedMapper, never()).insert(any(ProgramSupported.class));
  }

  @Test
  public void shouldNotUpdateIfAttributesDoesNotChanged() throws Exception {
    Long facilityId = 100L;
    Program program = new Program(1232L);
    Program program2 = new Program(555L);
    Date nullDate = null;

    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedProgram, program)));
    ProgramSupported programSupported2 = make(a(defaultProgramSupported, with(supportedProgram, program2), with(startDate, nullDate)));

    Facility facility = make(a(defaultFacility, with(programSupportedList, new LinkedList<>(asList(programSupported, programSupported2)))));
    facility.setId(facilityId);

    List<ProgramSupported> previouslyProgramSupportedList = new LinkedList<>(asList(programSupported, programSupported2));
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);


    assertFalse(programSupportedRepository.updateSupportedPrograms(facility));


    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper, never()).delete(anyLong(), anyLong());
    verify(programSupportedMapper, never()).insert(any(ProgramSupported.class));
  }

  @Test
  public void shouldUpdateIfAttributesChanged() throws Exception {
    Long facilityId = 100L;
    Program program = new Program(1232L);

    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedProgram, program)));
    Date date = new Date();
    ProgramSupported editedProgramSupported = new ProgramSupported(program.getId(), false, date);

    Facility facility = make(a(defaultFacility, with(programSupportedList, new LinkedList<>(asList(editedProgramSupported)))));
    facility.setId(facilityId);

    List<ProgramSupported> previouslyProgramSupportedList = new LinkedList<>(asList(programSupported));
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);


    assertTrue(programSupportedRepository.updateSupportedPrograms(facility));


    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper, never()).delete(anyLong(), anyLong());
    verify(programSupportedMapper, never()).insert(any(ProgramSupported.class));

    verify(programSupportedMapper).update(argThat(programSupportedMatcher(facilityId, editedProgramSupported.getActive(),
      editedProgramSupported.getStartDate(), facility.getModifiedBy(), null)));

  }

  @Test
  public void shouldGetAllSupportedProgramsForFacility() throws Exception {
    final Facility facility = make(a(defaultFacility));
    facility.setId(1L);

    final ProgramSupported hivProgram = make(a(defaultProgramSupported, with(supportedProgram, new Program(1L)),
      with(supportedFacilityId, facility.getId())));
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()))));
      add(hivProgram);
    }};
    when(programSupportedMapper.getAllByFacilityId(facility.getId())).thenReturn(programs);

    facility.setSupportedPrograms(programs);

    assertThat(programSupportedRepository.getAllByFacilityId(facility.getId()), is(programs));
    verify(programSupportedMapper).getAllByFacilityId(facility.getId());
  }

  @Test
  public void shouldReturnProgramSupportedByProgramAndFacilityId() throws Exception {
    programSupportedRepository.getByFacilityIdAndProgramId(1L, 1L);
    when(programSupportedMapper.getBy(anyLong(), anyLong())).thenReturn(new ProgramSupported());
    verify(programSupportedMapper).getBy(1L, 1L);
  }

  @Test
  public void shouldUpdateProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    doNothing().when(programSupportedMapper).update(programSupported);
    programSupportedRepository.updateSupportedProgram(programSupported);

    verify(programSupportedMapper).update(programSupported);
  }

  @Test
  public void shouldGetAciveByFacilityId() throws Exception {

    Long facilityId = 1L;
    List<ProgramSupported> programSupported = asList(new ProgramSupported());
    when(programSupportedMapper.getActiveProgramsByFacilityId(facilityId)).thenReturn(programSupported);

    programSupportedRepository.getActiveByFacilityId(facilityId);

    verify(programSupportedMapper).getActiveProgramsByFacilityId(facilityId);

  }

  private static Matcher<ProgramSupported> programSupportedMatcher(final Long facilityId, final Boolean active,
                                                                   final Date startDate, final Long modifiedBy,
                                                                   final Long createdBy) {
    return new ArgumentMatcher<ProgramSupported>() {
      @Override
      public boolean matches(Object argument) {
        ProgramSupported ps = (ProgramSupported) argument;
        return ps.getFacilityId() == facilityId && ps.getModifiedBy() == modifiedBy &&
          ps.getStartDate().equals(startDate) && ps.getActive().equals(active) && ps.getCreatedBy() == createdBy;
      }
    };
  }
}
