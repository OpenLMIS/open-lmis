package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProgramRepositoryTest {

    private ProgramMapper programMapper;
    private ProgramRepository programRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        programMapper = mock(ProgramMapper.class);
        programRepository = new ProgramRepository(programMapper, null, null);
    }

    @Test
    public void shouldReturnIdForTheGivenCode() {
        when(programMapper.getIdForCode("ABC")).thenReturn(10);
        assertThat(programRepository.getIdForCode("ABC"), is(10));
    }

    @Test
    public void shouldThrowExceptionWhenCodeDoesNotExist() {
        when(programMapper.getIdForCode("ABC")).thenReturn(null);
        exception.expect(DataException.class);
        exception.expectMessage("Invalid Program Code");
        programRepository.getIdForCode("ABC");
    }
}
