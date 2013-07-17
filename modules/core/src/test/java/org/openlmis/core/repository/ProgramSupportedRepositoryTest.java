/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.programSupportedList;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class ProgramSupportedRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProgramSupportedMapper programSupportedMapper;

  private ProgramSupportedRepository programSupportedRepository;
  private DateTime now;

  @Before
  public void setUp() throws Exception {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
    programSupportedRepository = new ProgramSupportedRepository(programSupportedMapper);
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

    verify(programSupportedMapper).add(programSupported);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    Program program = new Program();
    program.setCode("program code");
    programSupported.setProgram(program);

    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(
      programSupportedMapper).add(programSupported);

    expectedEx.expect(dataExceptionMatcher("error.facility.program.mapping.exists"));

    programSupportedRepository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldAddSupportedProgramsForFacility() throws Exception {
    Facility facility = new Facility();
    ArrayList<ProgramSupported> supportedPrograms = new ArrayList<>();
    ProgramSupported firstProgramSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported));
    ProgramSupported secondProgramSupported = new ProgramSupported();
    supportedPrograms.add(firstProgramSupported);
    supportedPrograms.add(secondProgramSupported);
    facility.setSupportedPrograms(supportedPrograms);

    programSupportedRepository.addSupportedProgramsFor(facility);

    verify(programSupportedMapper).add(firstProgramSupported);
    verify(programSupportedMapper).add(secondProgramSupported);
  }

  @Test
  public void shouldInsertIfDoestNotExist() throws Exception {

    ProgramSupported programSupported = make(a(defaultProgramSupported));

    Facility facility = make(a(defaultFacility, with(programSupportedList, asList(programSupported))));
    Long facilityId = 100L;

    facility.setId(facilityId);
    List<ProgramSupported> previouslyProgramSupportedList = new ArrayList<>();
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);


    programSupportedRepository.updateSupportedPrograms(facility);

    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper).add(programSupported);

  }

  @Test
  public void shouldDeleteIfNotSendInUpdate() throws Exception {
    Long facilityId = 100L;
    Program program = new Program(1232L, "random");

    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedProgram, program)));
    Facility facility = make(a(defaultFacility));
    facility.setId(facilityId);

    List<ProgramSupported> previouslyProgramSupportedList = asList(programSupported);
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);

    programSupportedRepository.updateSupportedPrograms(facility);

    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper).delete(facilityId, program.getId());
    verify(programSupportedMapper, never()).add(any(ProgramSupported.class));
  }

  @Test
  public void shouldUpdateIfExist() throws Exception {
    Long facilityId = 100L;
    Program program = new Program(1232L, "random");

    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedProgram, program)));
    Date date = new Date();
    ProgramSupported editedProgramSupported = new ProgramSupported(program.getId(), false, date);

    Facility facility = make(a(defaultFacility, with(programSupportedList, new LinkedList<>(asList(editedProgramSupported)))));
    facility.setId(facilityId);

    List<ProgramSupported> previouslyProgramSupportedList = new LinkedList<>(asList(programSupported));
    when(programSupportedMapper.getAllByFacilityId(facilityId)).thenReturn(previouslyProgramSupportedList);


    programSupportedRepository.updateSupportedPrograms(facility);


    verify(programSupportedMapper).getAllByFacilityId(facilityId);
    verify(programSupportedMapper, never()).delete(anyLong(), anyLong());
    verify(programSupportedMapper, never()).add(any(ProgramSupported.class));

    ProgramSupported savedProgramSupported = programSupportedSentToMapper(facilityId, editedProgramSupported, facility);
    verify(programSupportedMapper).update(savedProgramSupported);
  }


  @Test
  public void shouldGetAllSupportedProgramsForFacility() throws Exception {
    final Facility facility = make(a(defaultFacility));
    facility.setId(1L);

    final ProgramSupported hivProgram = make(a(defaultProgramSupported, with(supportedProgram, new Program(1L, "HIV")),
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


  private ProgramSupported programSupportedSentToMapper(Long facilityId, ProgramSupported editedProgramSupported, Facility facility) {
    ProgramSupported savedProgramSupported = new ProgramSupported(editedProgramSupported.getProgram().getId(),
      editedProgramSupported.getActive(), editedProgramSupported.getStartDate());
    savedProgramSupported.setFacilityId(facilityId);
    savedProgramSupported.setModifiedBy(facility.getModifiedBy());
    return savedProgramSupported;
  }

}
