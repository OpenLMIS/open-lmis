package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
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
    when(service.getByFacilityIdAndProgramId(1L, 1L)).thenReturn(null);

    service.uploadSupportedProgram(programSupported);

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
