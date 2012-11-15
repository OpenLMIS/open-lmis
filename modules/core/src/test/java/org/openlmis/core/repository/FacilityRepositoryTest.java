package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FacilityRepositoryTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private FacilityMapper mockedMapper;

    private FacilityRepository repository;

    @Before
    public void setUp() {
        repository = new FacilityRepository(mockedMapper);
    }

    @Test
    public void shouldInsertProduct() throws Exception {
        Facility facility = new Facility();
        repository.save(facility);
        verify(mockedMapper).insert(facility);
    }

    @Test
    public void shouldRaiseDuplicateProductCodeError() throws Exception {
        Facility facility = new Facility();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Facility Code found");
        doThrow(new DuplicateKeyException("")).when(mockedMapper).insert(facility);
        repository.save(facility);
    }

    @Test
    public void shouldRaiseIncorrectReferenceDataError() throws Exception {
        Facility facility = new Facility();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Missing Reference data");
        doThrow(new DataIntegrityViolationException("foreign key")).when(mockedMapper).insert(facility);
        repository.save(facility);
    }
}
