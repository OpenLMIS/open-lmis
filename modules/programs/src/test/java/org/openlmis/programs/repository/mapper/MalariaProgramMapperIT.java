package org.openlmis.programs.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.helpers.MalariaProgramBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-programs.xml")
@Transactional
@TransactionConfiguration(transactionManager = "openLmisTransactionManager")
public class MalariaProgramMapperIT {

    @Autowired
    private MalariaProgramMapper mapper;

    @Test
    public void shouldInsert() {
        MalariaProgram malariaProgram = MalariaProgramBuilder.fresh().build();
        int count = mapper.insert(malariaProgram);
        assertThat(count, is(not(0)));
        assertThat(malariaProgram.getId(), is(not(0)));
    }
}