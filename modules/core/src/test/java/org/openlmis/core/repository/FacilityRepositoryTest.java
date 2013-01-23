package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
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
import static org.openlmis.core.builder.ProgramBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class FacilityRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  @SuppressWarnings("unused")
  private FacilityMapper mockedFacilityMapper;

  @Mock
  @SuppressWarnings("unused")
  private ProgramSupportedMapper mockedProgramSupportedMapper;

  @Mock
  @SuppressWarnings("unused")
  private ProgramMapper mockedProgramMapper;

  private FacilityRepository repository;
  private DateTime now;

  @Before
  public void setUp() {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
    when(mockedFacilityMapper.isGeographicZonePresent(FacilityBuilder.GEOGRAPHIC_ZONE_ID)).thenReturn(Boolean.TRUE);
    repository = new FacilityRepository(mockedFacilityMapper, mockedProgramSupportedMapper, mockedProgramMapper, null);
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(defaultFacility));

    when(mockedFacilityMapper.insert(facility)).thenReturn(1);
    repository.save(facility);
    assertThat(facility.getModifiedDate(), is(now.toDate()));
    verify(mockedFacilityMapper).insert(facility);
  }

  @Test
  public void shouldAddSupportedProgram() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    int facilityId = 222;
    int programId = 111;
    when(mockedFacilityMapper.getIdForCode("facility code")).thenReturn(facilityId);
    when(mockedProgramMapper.getIdForCode("program code")).thenReturn(programId);

    repository.addSupportedProgram(programSupported);

    assertThat(programSupported.getModifiedDate(), is(now.toDate()));
    assertThat(programSupported.getFacilityId(), is(facilityId));
    assertThat(programSupported.getProgramId(), is(programId));

    verify(mockedProgramSupportedMapper).addSupportedProgram(programSupported);
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
    verify(mockedProgramSupportedMapper, times(2)).addSupportedProgram(any(ProgramSupported.class));
  }

  @Test
  public void shouldRaiseDuplicateFacilityCodeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Facility Code found");
    doThrow(new DuplicateKeyException("")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("foreign key")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    when(mockedFacilityMapper.getFacilityTypeIdForCode("facility code")).thenReturn(1);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program");
    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(mockedProgramSupportedMapper).addSupportedProgram(programSupported);
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Incorrect data length");
    doThrow(new DataIntegrityViolationException("value too long")).when(mockedFacilityMapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceDataOperatedByError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("invalid code");
    when(mockedFacilityMapper.getOperatedByIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Operated By'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityOperatorIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("valid code");
    when(mockedFacilityMapper.getOperatedByIdForCode("valid code")).thenReturn(1);

    repository.save(facility);
    assertThat(facility.getOperatedBy().getId(), is(1));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataFacilityTypeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("invalid code");
    when(mockedFacilityMapper.getFacilityTypeIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceGeographicZoneIdError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getGeographicZone().setId(999);
    when(mockedFacilityMapper.isGeographicZonePresent(999)).thenReturn(Boolean.FALSE);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Geographic Zone Id'");
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingMandatoryReferenceDataFacilityType() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing mandatory reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityTypeIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("valid code");
    when(mockedFacilityMapper.getFacilityTypeIdForCode("valid code")).thenReturn(1);

    repository.save(facility);
    assertThat(facility.getFacilityType().getId(), is(1));
  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("invalid Code");
    programSupported.setProgramCode("valid Code");

    when(mockedFacilityMapper.getIdForCode("invalid Code")).thenThrow(new DataException("Invalid Facility Code"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = new Facility();
    when(mockedFacilityMapper.getById(1)).thenReturn(facility);
    Integer id = 1;
    facility.setId(id);
    List<Program> programs = new ArrayList<>();
    when(mockedProgramMapper.getByFacilityId(1)).thenReturn(programs);
    Facility facility1 = repository.getById(1);

    assertThat(facility1.getSupportedPrograms(), is(programs));
    verify(mockedFacilityMapper).getById(1);
    verify(mockedProgramMapper).getByFacilityId(1);

  }

  @Test
  public void shouldUpdateFacilityIfIDIsSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1);

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
    facility.setId(1);

    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "HIV"), with(programId, 1))));
    }};

    facility.setSupportedPrograms(programs);

    List<Program> programsForFacility = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "ARV"), with(programId, 2))));
    }};

    when(mockedProgramMapper.getByFacilityId(facility.getId())).thenReturn(programsForFacility);

    repository.save(facility);

    verify(mockedProgramMapper).getByFacilityId(facility.getId());
    verify(mockedProgramSupportedMapper).addSupportedProgram(new ProgramSupported(facility.getId(), 1, true, null, facility.getModifiedDate(), facility.getModifiedBy()));
    verify(mockedProgramSupportedMapper).delete(facility.getId(), 2);
  }

  @Test
  public void shouldUpdateDataReportableActiveFlag() {

    Facility facility = make(a(defaultFacility));
    repository.updateDataReportableAndActiveFor(facility);
    verify(mockedFacilityMapper).updateDataReportableAndActiveFor(facility);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(mockedFacilityMapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(repository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    Mockito.when(mockedFacilityMapper.getIdForCode("ABC")).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    repository.getIdForCode("ABC");
  }
}