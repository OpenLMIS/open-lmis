package org.openlmis.programs.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.helpers.MalariaProgramBuilder;
import org.openlmis.programs.repository.mapper.MalariaProgramMapper;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.inOrder;
import static org.openlmis.programs.helpers.ImplementationBuilder.createRandomImplementations;

public class MalariaProgramRepositoryTest {

    @Captor
    private ArgumentCaptor<ArrayList<Implementation>> captor;
    @Spy
    private MalariaProgramMapper malariaProgramMapper = new MalariaProgramDummyMapper();
    @Mock
    private ImplementationRepository implementationRepository;
    @InjectMocks
    MalariaProgramRepository repository;

    private MalariaProgram malariaProgram;
    private List<Implementation> implementations;
    private int expectedMalariaProgramId = nextInt();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        implementations = createRandomImplementations();
        malariaProgram = MalariaProgramBuilder.fresh().build();
        malariaProgram.setImplementations(implementations);
    }

    @Test
    public void shouldReturnInsertedMalariaPrograms() {
        MalariaProgram actual = repository.save(malariaProgram);
        assertThat(actual.getId(), is(expectedMalariaProgramId));
    }

    @Test
    public void shouldInsertMalariaProgramInCascade() {
        repository.save(malariaProgram);
        InOrder inOrder = inOrder(malariaProgramMapper, implementationRepository);
        inOrder.verify(malariaProgramMapper).insert(malariaProgram);
        inOrder.verify(implementationRepository).save(captor.capture());
        assertThat(captor.getValue(), is(implementations));
        for (Implementation implementation : captor.getValue()) {
            assertThat(implementation.getMalariaProgram(), is(malariaProgram));
        }
    }

    private class MalariaProgramDummyMapper implements MalariaProgramMapper {

        @Override
        public int insert(MalariaProgram malariaProgram) {
            malariaProgram.setId(expectedMalariaProgramId);
            return 0;
        }
    }
}
