package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
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
        Facility facility = new Facility();

        repository.saveOrUpdate(facility);
        assertThat(facility.getModifiedDate(), is(now.toDate()));
        verify(mockedFacilityMapper).insert(facility);
    }

    @Test
    public void shouldAddSupportedProgram() throws Exception {
        ProgramSupported programSupported = new ProgramSupported();

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
        repository.saveOrUpdate(facility);
        verify(programSupportedMapper, times(2)).addSupportedProgram(any(ProgramSupported.class));
    }

    @Test
    public void shouldRaiseDuplicateFacilityCodeError() throws Exception {
        Facility facility = new Facility();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Facility Code found");
        doThrow(new DuplicateKeyException("")).when(mockedFacilityMapper).insert(facility);
        repository.saveOrUpdate(facility);
    }

    @Test
    public void shouldRaiseIncorrectReferenceDataError() throws Exception {
        Facility facility = new Facility();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Missing Reference data");
        doThrow(new DataIntegrityViolationException("foreign key")).when(mockedFacilityMapper).insert(facility);
        repository.saveOrUpdate(facility);
    }

    @Test
    public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
        ProgramSupported programSupported = new ProgramSupported();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Facility has already been mapped to the program");
        doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(programSupportedMapper).addSupportedProgram(programSupported);
        repository.addSupportedProgram(programSupported);
    }

    @Test
    public void shouldGetFacilityById() throws Exception {
        Facility facility = new Facility();
        when(mockedFacilityMapper.get(1)).thenReturn(facility);
        int id = 1;
        facility.setId(id);
        List<Program> programs = new ArrayList<>();
        when(programMapper.getByFacilityId(1)).thenReturn(programs);
        Facility facility1 = repository.getFacility(1);

        assertThat(facility1.getSupportedPrograms(), is(programs));
        verify(mockedFacilityMapper).get(1);
        verify(programMapper).getByFacilityId(1);

    }

    @Test
    public void shouldUpdateFacilityIfIDIsSet() throws Exception {
        Facility facility = new Facility();
        facility.setId(1);

        repository.saveOrUpdate(facility);
        verify(mockedFacilityMapper).update(facility);
        verify(mockedFacilityMapper, never()).insert(facility);
    }

    @Test
    public void shouldNotUpdateFacilityIfIDIsNotSet() throws Exception {
        Facility facility = new Facility();
        repository.saveOrUpdate(facility);
        verify(mockedFacilityMapper, never()).update(facility);
    }

    @Test
    public void shouldUpdateSupportedProgramsForFacilityIfIDIsDefined() throws Exception {
        Facility facility = make(a(defaultFacility));
        facility.setId(1);
        List<Program> programs = new ArrayList<Program>() {{
            add(make(a(ProgramBuilder.defaultProgram)));
            add(make(a(ProgramBuilder.defaultProgram, with(programCode, "HIV"))));
        }};

        facility.setSupportedPrograms(programs);

        List<Program> programsForFacility = new ArrayList<Program>() {{
            add(make(a(ProgramBuilder.defaultProgram)));
            add(make(a(ProgramBuilder.defaultProgram, with(programCode, "ARV"))));
        }};

        when(programMapper.getByFacilityId(facility.getId())).thenReturn(programsForFacility);


        repository.saveOrUpdate(facility);

        verify(programMapper).getByFacilityId(facility.getId());
        verify(programSupportedMapper).addSupportedProgram(new ProgramSupported(facility.getCode(),"HIV",true, facility.getModifiedBy(), facility.getModifiedDate()));
        verify(programSupportedMapper).deleteObsoletePrograms(facility.getId(),"ARV");
    }

}