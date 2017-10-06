package org.openlmis.programs.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.helpers.ImplementationBuilder;
import org.openlmis.programs.helpers.MalariaProgramBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-programs.xml")
@Transactional
@TransactionConfiguration(transactionManager = "openLmisTransactionManager")
public class ImplementationMapperIT {

    @Autowired
    private ImplementationMapper mapper;
    @Autowired
    private MalariaProgramMapper malariaProgramMapper;

    private MalariaProgram malariaProgram = MalariaProgramBuilder.fresh().build();

    @Test
    public void shouldInsert() {
        malariaProgramMapper.insert(malariaProgram);
        Implementation implementation = ImplementationBuilder.fresh().setMalariaProgram(malariaProgram).build();
        int count = mapper.insert(implementation);
        assertThat(count, is(not(0)));
        assertThat(implementation.getId(), is(not(0)));
    }

    @Test
    public void shouldBulkInsert() {
        malariaProgramMapper.insert(malariaProgram);
        List<Implementation> implementations = createRandomPrograms();
        int count = mapper.bulkInsert(implementations);
        assertThat(count, is(implementations.size()));
        for (Implementation implementation : implementations) {
            assertThat(implementation.getId(), is(not(0)));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenMalariaProgramDoesNotExist() {
        MalariaProgram malariaProgram = MalariaProgramBuilder.fresh().build();
        Implementation implementation = ImplementationBuilder.fresh().setMalariaProgram(malariaProgram).build();
        mapper.insert(implementation);
    }

    private List<Implementation> createRandomPrograms() {
        List<Implementation> result = new ArrayList<>();
        int randomQuantity = nextInt(10) + 1;
        for (int i = 0; i < randomQuantity; i++) {
            result.add(ImplementationBuilder.fresh().setMalariaProgram(malariaProgram).build());
        }
        return result;
    }
}
