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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Arrays;
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
  private FacilityMapper mapper;
  @Mock
  private ProgramSupportedRepository programSupportedRepository;
  @Mock
  private ProgramRepository programRepository;

  private FacilityRepository repository;
  private DateTime now;

  @Before
  public void setUp() {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
    when(mapper.isGeographicZonePresent(FacilityBuilder.GEOGRAPHIC_ZONE_ID)).thenReturn(Boolean.TRUE);
    repository = new FacilityRepository(mapper, programSupportedRepository, programRepository, null);
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(defaultFacility));

    when(mapper.insert(facility)).thenReturn(1);
    repository.save(facility);
    assertThat(facility.getModifiedDate(), is(now.toDate()));
    verify(mapper).insert(facility);
  }

  @Test
  public void shouldAddSupportedProgram() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    int facilityId = 222;
    int programId = 111;
    when(mapper.getIdForCode("facility code")).thenReturn(facilityId);
    when(programRepository.getIdByCode("program code")).thenReturn(programId);

    repository.addSupportedProgram(programSupported);

    assertThat(programSupported.getModifiedDate(), is(now.toDate()));
    assertThat(programSupported.getFacilityId(), is(facilityId));
    assertThat(programSupported.getProgramId(), is(programId));

    verify(programSupportedRepository).addSupportedProgram(programSupported);
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
    verify(programSupportedRepository, times(2)).addSupportedProgram(any(ProgramSupported.class));
  }

  @Test
  public void shouldRaiseDuplicateFacilityCodeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Facility Code found");
    doThrow(new DuplicateKeyException("")).when(mapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("foreign key")).when(mapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    when(mapper.getFacilityTypeIdForCode("facility code")).thenReturn(1);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program");
    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(programSupportedRepository).addSupportedProgram(programSupported);
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    Facility facility = make(a(defaultFacility));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Incorrect data length");
    doThrow(new DataIntegrityViolationException("value too long")).when(mapper).insert(facility);
    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceDataOperatedByError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("invalid code");
    when(mapper.getOperatedByIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Operated By'");
    repository.save(facility);
  }

  @Test
  public void shouldSetFacilityOperatorIdWhenCodeIsValid() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getOperatedBy().setCode("valid code");
    when(mapper.getOperatedByIdForCode("valid code")).thenReturn(1);

    repository.save(facility);
    assertThat(facility.getOperatedBy().getId(), is(1));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataFacilityTypeError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getFacilityType().setCode("invalid code");
    when(mapper.getFacilityTypeIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Facility Type'");
    repository.save(facility);
  }

  @Test
  public void shouldRaiseInvalidReferenceGeographicZoneIdError() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.getGeographicZone().setId(999);
    when(mapper.isGeographicZonePresent(999)).thenReturn(Boolean.FALSE);

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
    when(mapper.getFacilityTypeIdForCode("valid code")).thenReturn(1);

    repository.save(facility);
    assertThat(facility.getFacilityType().getId(), is(1));
  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("invalid Code");
    programSupported.setProgramCode("valid Code");

    when(mapper.getIdForCode("invalid Code")).thenThrow(new DataException("Invalid Facility Code"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = new Facility();
    when(mapper.getById(1)).thenReturn(facility);
    Integer id = 1;
    facility.setId(id);
    List<Program> programs = new ArrayList<>();
    when(programRepository.getByFacility(1)).thenReturn(programs);
    Facility facility1 = repository.getById(1);

    assertThat(facility1.getSupportedPrograms(), is(programs));
    verify(mapper).getById(1);
    verify(programRepository).getByFacility(1);
  }

  @Test
  public void shouldUpdateFacilityIfIDIsSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1);

    repository.save(facility);
    verify(mapper).update(facility);
    verify(mapper, never()).insert(facility);
  }

  @Test
  public void shouldNotUpdateFacilityIfIDIsNotSet() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(null);
    repository.save(facility);
    verify(mapper, never()).update(facility);
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

    when(programRepository.getByFacility(facility.getId())).thenReturn(programsForFacility);

    repository.save(facility);

    verify(programRepository).getByFacility(facility.getId());
    verify(programSupportedRepository).addSupportedProgram(new ProgramSupported(facility.getId(), 1, true, now.toDate(), facility.getModifiedDate(), facility.getModifiedBy()));
    verify(programSupportedRepository).deleteSupportedPrograms(facility.getId(), 2);
  }

  @Test
  public void shouldUpdateDataReportableActiveFlag() {

    Facility facility = make(a(defaultFacility));
    repository.updateDataReportableAndActiveFor(facility);
    verify(mapper).updateDataReportableAndActiveFor(facility);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(mapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(repository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    Mockito.when(mapper.getIdForCode("ABC")).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(mapper.searchFacilitiesByCodeOrName("query")).thenReturn(facilityList);

    List<Facility> returnedFacilities = repository.searchFacilitiesByCodeOrName("query");

    assertThat(returnedFacilities, is(facilityList));
  }
}