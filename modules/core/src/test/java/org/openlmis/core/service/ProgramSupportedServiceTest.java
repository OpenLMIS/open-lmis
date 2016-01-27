/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.core.service;

import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.programSupportedList;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(ProgramSupportedService.class)
public class ProgramSupportedServiceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @InjectMocks
  ProgramSupportedService service;

  @Mock
  FacilityService facilityService;

  @Mock
  ProgramService programService;

  @Mock
  ProgramSupportedRepository repository;


  @Mock
  FacilityProgramProductService facilityProgramProductService;

  @Mock
  private EventService eventService;


  @Test
  public void shouldNotGiveErrorIfSupportedProgramWithActiveFalseAndDateNotProvided() throws Exception {
    ProgramSupported programSupported = createSupportedProgram("facility code", "program code", false, null);

    Long facilityId = 222L;
    Long programId = 111L;
    Facility facility = new Facility();
    facility.setCode("facility code");
    when(facilityService.getByCode(facility)).thenReturn(new Facility(facilityId));
    when(programService.getByCode("program code")).thenReturn(new Program(programId));

    service.uploadSupportedProgram(programSupported);

    assertThat(programSupported.getFacilityId(), is(facilityId));
    assertThat(programSupported.getProgram().getId(), is(programId));
    assertThat(programSupported.getActive(), is(false));
    assertThat(programSupported.getStartDate(), is(nullValue()));

  }


  @Test
  public void shouldGiveErrorIfSupportedProgramWithActiveTrueAndStartDateNotProvided() throws Exception {
    ProgramSupported program = createSupportedProgram("facility code", "program code", true, null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("supported.programs.invalid");

    service.uploadSupportedProgram(program);
  }

  @Test
  public void shouldNotGiveErrorIfProgramSupportedIsActiveAndDateProvided() throws Exception {
    String facilityCode = "some facility";
    String programCode = "some program";
    Date startDate = new Date();
    ProgramSupported program = createSupportedProgram(facilityCode, programCode, true, startDate);
    Long facilityId = 222L;
    Long programId = 111L;
    Facility facility = new Facility();
    facility.setCode(facilityCode);
    when(facilityService.getByCode(facility)).thenReturn(new Facility(facilityId));
    when(programService.getByCode(programCode)).thenReturn(new Program(programId));

    service.uploadSupportedProgram(program);

    assertThat(program.getFacilityId(), is(facilityId));
    assertThat(program.getProgram().getId(), is(programId));
    assertThat(program.getActive(), is(true));
    assertThat(program.getStartDate(), is(startDate));

  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = createSupportedProgram("invalid Code", "valid Code", true, new Date());

    Facility facility = new Facility();
    facility.setCode("invalid Code");
    doThrow(new DataException("error.facility.code.invalid")).when(facilityService).getByCode(facility);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    service.uploadSupportedProgram(programSupported);
  }


  @Test
  public void shouldInsertProgramSupportedIfDoesNotExist() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("F1");
    Program program = new Program();
    program.setCode("P1");
    programSupported.setProgram(program);
    programSupported.setModifiedDate(new Date());
    Facility facility = new Facility();
    facility.setCode("F1");
    when(facilityService.getByCode(facility)).thenReturn(new Facility(1L));
    when(programService.getByCode("P1")).thenReturn(new Program(1L));

    service.uploadSupportedProgram(programSupported);

  }

  @Test
  public void shouldGetProgramsSupportedFilledWithISAs() throws Exception {
    Long programId = 2L;
    Long facilityId = 1L;

    List<FacilityProgramProduct> products = new ArrayList<>();

    when(facilityProgramProductService.getForProgramAndFacility(programId, facilityId)).thenReturn(products);
    ProgramSupported expectedProgram = new ProgramSupported();
    when(repository.getByFacilityIdAndProgramId(facilityId, programId)).thenReturn(expectedProgram);

    ProgramSupported returnedProgram = service.getFilledByFacilityIdAndProgramId(facilityId, programId);

    verify(facilityProgramProductService).getForProgramAndFacility(programId, facilityId);

    assertThat(returnedProgram, is(expectedProgram));
    assertThat(returnedProgram.getProgramProducts(), is(products));
  }

  @Test
  public void shouldUpdateProgramSupportedIfItExists() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("F1");
    Program program = new Program();
    program.setCode("P1");
    programSupported.setProgram(program);
    programSupported.setId(1L);
    Facility facility = new Facility();
    facility.setCode("F1");
    when(facilityService.getByCode(facility)).thenReturn(new Facility(1L));
    when(programService.getByCode("P1")).thenReturn(new Program(2L));

    service.uploadSupportedProgram(programSupported);

    assertThat(programSupported.getFacilityId(), is(1L));
    assertThat(programSupported.getProgram().getId(), is(2L));
  }

  @Test
  public void shouldReturnProgramSupported() throws Exception {
    ProgramSupported expectedProgram = new ProgramSupported();
    when(repository.getByFacilityIdAndProgramId(1L, 2L)).thenReturn(expectedProgram);

    ProgramSupported programSupported = service.getByFacilityIdAndProgramId(1L, 2L);

    assertThat(programSupported, is(expectedProgram));
    verify(repository).getByFacilityIdAndProgramId(1L, 2L);
  }

  @Test
  public void shouldThrowExceptionIfFacilityDoesNotExistWhileGettingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    String fCode = "FCode";
    String pCode = "PCode";
    programSupported.setFacilityCode(fCode);
    Program program = new Program();
    program.setCode(pCode);
    programSupported.setProgram(program);
    Facility facility = new Facility();
    facility.setCode(fCode);
    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    service.getProgramSupported(programSupported);
  }

  @Test
  public void shouldThrowExceptionIfProgramDoesNotExistWhileGettingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    String fCode = "FCode";
    String pCode = "PCode";
    programSupported.setFacilityCode(fCode);
    Program program = new Program();
    program.setCode(pCode);
    programSupported.setProgram(program);
    Facility facility = new Facility();
    facility.setCode(fCode);
    when(facilityService.getByCode(facility)).thenReturn(facility);
    when(programService.getByCode(pCode)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("program.code.invalid");

    service.getProgramSupported(programSupported);
  }

  @Test
  public void shouldUpdateSupportedProgramsAndVirtualIfParentChanged() throws Exception {
    List<ProgramSupported> programsSupported = asList(make(a(defaultProgramSupported)));
    Facility facility = make(a(defaultFacility, with(programSupportedList, programsSupported)));

    ProgramSupportedEventDTO programSupportedEventDTO = mock(ProgramSupportedEventDTO.class);
    Event event = mock(Event.class);

    whenNew(ProgramSupportedEventDTO.class).withArguments(facility.getCode(), programsSupported).thenReturn(programSupportedEventDTO);
    when(programSupportedEventDTO.createEvent()).thenReturn(event);
    when(repository.updateSupportedPrograms(facility)).thenReturn(true);

    service.updateSupportedPrograms(facility);

    verify(repository).updateSupportedPrograms(facility);
    verify(repository).updateForVirtualFacilities(facility);
    verify(eventService).notify(event);
  }

  @Test
  public void shouldNotUpdateSupportedProgramsForVirtualIfParentNotChanged() throws Exception {
    List<ProgramSupported> programsSupported = asList(make(a(defaultProgramSupported)));
    Facility facility = make(a(defaultFacility, with(programSupportedList, programsSupported)));

    ProgramSupportedEventDTO programSupportedEventDTO = mock(ProgramSupportedEventDTO.class);
    Event event = mock(Event.class);

    whenNew(ProgramSupportedEventDTO.class).withArguments(facility.getCode(), programsSupported).thenReturn(programSupportedEventDTO);
    when(programSupportedEventDTO.createEvent()).thenReturn(event);
    when(repository.updateSupportedPrograms(facility)).thenReturn(false);

    service.updateSupportedPrograms(facility);

    verify(repository).updateSupportedPrograms(facility);
    verify(repository, never()).updateForVirtualFacilities(facility);
    verify(eventService, never()).notify(event);
  }

  @Test
  public void shouldGetActiveProgramsSupportedByFacilityId() throws Exception {

    Long facilityId = 1L;
    List<ProgramSupported> programSupported = asList(new ProgramSupported());
    when(repository.getActiveByFacilityId(facilityId)).thenReturn(programSupported);

    List<ProgramSupported> activeProgramSupported = service.getActiveByFacilityId(facilityId);

    verify(repository).getActiveByFacilityId(facilityId);
    assertThat(activeProgramSupported, equalTo(programSupported));

  }

  @Test
  public void shouldNotifyProgramsSupportedUpdateForParentAndVirtualFacility() throws Exception {
    Facility facility = mock(Facility.class);
    String facilityCode = "F1111";
    List<ProgramSupported> supportedPrograms = new ArrayList<>();
    when(facility.getCode()).thenReturn(facilityCode);
    when(facility.getSupportedPrograms()).thenReturn(supportedPrograms);
    ProgramSupportedEventDTO programSupportedEventDTO = mock(ProgramSupportedEventDTO.class);
    whenNew(ProgramSupportedEventDTO.class).withArguments(facilityCode, supportedPrograms).thenReturn(programSupportedEventDTO);
    Event event = mock(Event.class);
    when(programSupportedEventDTO.createEvent()).thenReturn(event);
    Facility mockVirtualFacility = mock(Facility.class);
    List<Facility> virtualFacilities = asList(mockVirtualFacility);
    when(facilityService.getChildFacilities(facility)).thenReturn(virtualFacilities);

    String virtualFacilityCode = "VVF1111";
    when(mockVirtualFacility.getCode()).thenReturn(virtualFacilityCode);
    ProgramSupportedEventDTO virtualProgramSupportedEventDTO = mock(ProgramSupportedEventDTO.class);
    whenNew(ProgramSupportedEventDTO.class).withArguments(virtualFacilityCode, supportedPrograms).thenReturn(virtualProgramSupportedEventDTO);
    Event virtualFacilityEvent = mock(Event.class);
    when(virtualProgramSupportedEventDTO.createEvent()).thenReturn(virtualFacilityEvent);

    service.notifyProgramSupportedUpdated(facility);

    verify(eventService).notify(event);
    verify(eventService).notify(virtualFacilityEvent);

  }

  private ProgramSupported createSupportedProgram(String facilityCode, String programCode, boolean active, Date startDate) {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode(facilityCode);
    Program program = new Program();
    program.setCode(programCode);
    programSupported.setProgram(program);
    programSupported.setActive(active);
    programSupported.setStartDate(startDate);
    return programSupported;
  }
}
