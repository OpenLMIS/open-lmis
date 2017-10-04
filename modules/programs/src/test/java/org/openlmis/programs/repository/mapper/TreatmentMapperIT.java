package org.openlmis.programs.repository.mapper;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;
import org.openlmis.programs.helpers.ImplementationBuilder;
import org.openlmis.programs.helpers.TreatmentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-programs.xml")
@Transactional
@TransactionConfiguration(transactionManager = "openLmisTransactionManager")
public class TreatmentMapperIT {

    @Autowired
    private TreatmentMapper mapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ImplementationMapper implementationMapper;
    private Treatment treatment;
    private Implementation implementation;

    @Before
    public void setUp() throws Exception {
        implementation = ImplementationBuilder.fresh().build();
        treatment = TreatmentBuilder.fresh()
                .setImplementation(implementation)
                .build();
    }

    @Test
    public void shouldInsert() throws Exception {
        productMapper.insert(treatment.getProduct());
        implementationMapper.insert(treatment.getImplementation());
        int count = mapper.insert(treatment);
        assertThat(count, is(not(0)));
        assertThat(treatment.getId(), is(not(0)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenProductDoesNotExist() {
        implementationMapper.insert(treatment.getImplementation());
        mapper.insert(treatment);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenImplementationDoesNotExist() {
        productMapper.insert(treatment.getProduct());
        mapper.insert(treatment);
    }
}
