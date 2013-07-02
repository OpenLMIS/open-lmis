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
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

  @Mock
  FacilityProgramProductService facilityProgramProductService;

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
  public void shouldReturnNullIfProgramNotSupportedByFacility() throws Exception {
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
