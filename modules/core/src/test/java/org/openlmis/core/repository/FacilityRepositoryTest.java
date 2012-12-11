package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramBuilder.programId;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class FacilityRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  @Mock
  private FacilityMapper mockedFacilityMapper;

  @Mock
  private ProgramSupportedMapper programSupportedMapper;

  @Mock
  private ProgramMapper programMapper;

  private FacilityRepository repository;
  private DateTime now;

  @Before
  public void setUp() {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);

    repository = new FacilityRepository(mockedFacilityMapper, programSupportedMapper, programMapper);
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(defaultFacility));

    when(mockedFacilityMapper.insert(facility)).thenReturn(1L);
    repository.save(facility);
    assertThat(facility.getModifiedDate(), is(now.toDate()));
    verify(mockedFacilityMapper).insert(facility);
    assertThat(facility.getId(), is(1L));
  }

  @Test
  public void shouldAddSupportedProgram() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    when(mockedFacilityMapper.getFacilityTypeIdForCode("facility code")).thenReturn(1L);

    repository.addSupportedProgram(programSupported);

    assertThat(programSupported.getModifiedDate(), is(now.toDate()));
    verify(programSupportedMapper).addSupportedProgram(programSupported);
  }

  @Test
  public void shouldAddProgramsSupportedByAFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(null);
    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(defaultProgram)));
      add(make(a(defaultProgram)));
    }};
    facility.setSupportedPrograms(programs);
    repository.save(facility);
    verify(programSupportedMapper, times(2)).addSupportedProgram(any(ProgramSupported.class));
  }

  @Test
  public void shouldRaiseDuplicateFacilityCodeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Duplicate Facility Code found");
    doThrow(new DuplicateKeyException("")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("foreign key")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    when(mockedFacilityMapper.getFacilityTypeIdForCode("facility code")).thenReturn(1L);

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program");
    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(programSupportedMapper).addSupportedProgram(programSupported);
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Incorrect data length");
    doThrow(new DataIntegrityViolationException("value too long")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceDataOperatedByError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("invalid code");
    when(mockedFacilityMapper.getOperatedByIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Invalid reference data 'Operated By'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityOperatorIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("valid code");
    when(mockedFacilityMapper.getOperatedByIdForCode("valid code")).thenReturn(1L);

    repository.save(facility);
    assertThat(facility.getOperatedBy().getId(), is(1L));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataFacilityTypeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("invalid code");
    when(mockedFacilityMapper.getFacilityTypeIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Invalid reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingMandatoryReferenceDataFacilityType() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("");

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing mandatory reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityTypeIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("valid code");
    when(mockedFacilityMapper.getFacilityTypeIdForCode("valid code")).thenReturn(1L);

    repository.save(facility);
    assertThat(facility.getFacilityType().getId(), is(1L));
  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("invalid Code");
    programSupported.setProgramCode("valid Code");

    when(mockedFacilityMapper.getFacilityTypeIdForCode("invalid Code")).thenReturn(null);
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Invalid reference data 'Facility Code'");
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldRaiseErrorWhenProgramSupportedIsSpecifiedWithoutFacilityCode() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("");
    programSupported.setProgramCode("valid Code");
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing reference data 'Facility Code'");
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = new Facility();
    when(mockedFacilityMapper.get(1L)).thenReturn(facility);
    Long id = 1L;
    facility.setId(id);
    List<Program> programs = new ArrayList<>();
    when(programMapper.getByFacilityId(1L)).thenReturn(programs);
    Facility facility1 = repository.getFacility(1L);

    assertThat(facility1.getSupportedPrograms(), is(programs));
    verify(mockedFacilityMapper).get(1L);
    verify(programMapper).getByFacilityId(1L);

  }

  @Test
  public void shouldUpdateFacilityIfIDIsSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1L);

    repository.save(facility);
    verify(mockedFacilityMapper).update(facility);
    verify(mockedFacilityMapper, never()).insert(facility);
  }

  @Test
  public void shouldNotUpdateFacilityIfIDIsNotSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(null);
    repository.save(facility);
    verify(mockedFacilityMapper, never()).update(facility);
  }

  @Test
  public void shouldUpdateSupportedProgramsForFacilityIfIdIsDefined() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1L);

    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "HIV"),with(programId, 1L))));
    }};

    facility.setSupportedPrograms(programs);

    List<Program> programsForFacility = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "ARV"), with(programId, 2L))));
    }};

    when(programMapper.getByFacilityId(facility.getId())).thenReturn(programsForFacility);

    repository.save(facility);

    verify(programMapper).getByFacilityId(facility.getId());
    verify(programSupportedMapper).addSupportedProgram(new ProgramSupported(facility.getId(), 1L, true, facility.getModifiedBy(), facility.getModifiedDate()));
    verify(programSupportedMapper).deleteObsoletePrograms(facility.getId(), 2L);
  }

    @Test
    public void shouldUpdateDataReportableActiveFlag(){

        Facility facility = make(a(defaultFacility));
        repository.updateDataReportableAndActiveFor(facility);
        verify(mockedFacilityMapper).updateDataReportableAndActiveFor(facility);

    }

}