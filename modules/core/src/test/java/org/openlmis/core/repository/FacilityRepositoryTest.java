package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class FacilityRepositoryTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Mock
    private FacilityMapper mockedMapper;

    private FacilityRepository repository;
    private DateTime now;

    @Before
    public void setUp() {
        mockStatic(DateTime.class);
        now = new DateTime(2012, 10, 10, 8, 0);
        when(DateTime.now()).thenReturn(now);

        repository = new FacilityRepository(mockedMapper, null);
    }

    @Test
    public void shouldInsertFacility() throws Exception {
        Facility facility = new Facility();

        repository.save(facility);
        assertThat(facility.getModifiedDate(), is(now.toDate()));
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
