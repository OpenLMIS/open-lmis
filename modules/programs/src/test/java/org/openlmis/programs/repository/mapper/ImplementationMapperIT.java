package org.openlmis.programs.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openlmis.programs.helpers.ImplementationBuilder.*;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.randomMalariaProgram;

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

    private MalariaProgram program = make(a(randomMalariaProgram));

    @Test
    public void shouldInsert() {
        malariaProgramMapper.insert(program);
        Implementation implementation = make(a(randomImplementation, with(malariaProgram, program)));
        int count = mapper.insert(implementation);
        assertThat(count, is(not(0)));
        assertThat(implementation.getId(), is(not(0)));
    }

    @Test
    public void shouldBulkInsert() {
        List<Implementation> implementations = createRandomImplementations();
        malariaProgramMapper.insert(implementations.get(0).getMalariaProgram());
        int count = mapper.bulkInsert(implementations);
        assertThat(count, is(implementations.size()));
        for (Implementation implementation : implementations) {
            assertThat(implementation.getId(), is(not(0)));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenMalariaProgramDoesNotExist() {
        Implementation implementation = make(a(randomImplementation, with(malariaProgram, program)));
        mapper.insert(implementation);
    }
}
