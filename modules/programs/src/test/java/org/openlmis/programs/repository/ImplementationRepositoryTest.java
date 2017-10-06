package org.openlmis.programs.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;
import org.openlmis.programs.repository.mapper.ImplementationMapper;
import org.openlmis.programs.repository.mapper.TreatmentMapper;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.inOrder;
import static org.openlmis.programs.helpers.ImplementationBuilder.createRandomImplementations;

public class ImplementationRepositoryTest {
    @Spy
    private TreatmentMapper treatmentMapper = new TreatmentDummyMapper();
    @Spy
    private ImplementationMapper implementationMapper = new ImplementationDummyMapper();
    @InjectMocks
    private ImplementationRepository repository = new ImplementationRepository();

    private int expectedImplementationId = nextInt();
    private int expectedTreatmentsId = nextInt();
    private List<Implementation> implementations;
    private List<Treatment> treatments = new ArrayList<>(10);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        implementations = createRandomImplementations();
        for (Implementation implementation : implementations) {
            treatments.addAll(implementation.getTreatments());
        }
    }

    @Test
    public void shouldReturnInsertedImplementations() {
        List<Implementation> actualImplementations = repository.save(implementations);
        assertThat(actualImplementations, is(implementations));
        for (Implementation implementation : actualImplementations) {
            assertThat(implementation.getId(), is(expectedImplementationId));
        }
    }

    @Test
    public void shouldReturnInsertedInnerTreatments() {
        List<Implementation> actualImplementations = repository.save(implementations);
        for (Implementation implementation : actualImplementations) {
            for (Treatment treatment : implementation.getTreatments()) {
                assertThat(treatment.getImplementation().getId(), is(expectedImplementationId));
                assertThat(treatment.getId(), is(expectedTreatmentsId));
            }
        }
    }

    @Test
    public void shouldSaveInCascade() throws Exception {
        InOrder inOrder = inOrder(implementationMapper, treatmentMapper);
        repository.save(implementations);
        inOrder.verify(implementationMapper).bulkInsert(implementations);
        inOrder.verify(treatmentMapper).bulkInsert((List<Treatment>) any());
    }

    private class TreatmentDummyMapper implements TreatmentMapper {

        @Override
        public int insert(Treatment treatment) {
            return 0;
        }

        @Override
        public int bulkInsert(List<Treatment> treatments) {
            for (Treatment treatment : treatments) {
                treatment.setId(expectedTreatmentsId);
            }
            return 0;
        }
    }

    private class ImplementationDummyMapper implements ImplementationMapper {

        @Override
        public int insert(Implementation implementation) {
            return 0;
        }

        @Override
        public int bulkInsert(List<Implementation> implementations) {
            for (Implementation implementation : implementations) {
                implementation.setId(expectedImplementationId);
            }
            return 0;
        }
    }
}