package org.openlmis.programs.repository.mapper;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.domain.malaria.Treatment;
import org.openlmis.programs.helpers.TreatmentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.randomProduct;
import static org.openlmis.programs.helpers.ImplementationBuilder.malariaProgram;
import static org.openlmis.programs.helpers.ImplementationBuilder.randomImplementation;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.randomMalariaProgram;
import static org.openlmis.programs.helpers.TreatmentBuilder.randomTreatment;

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
    private MalariaProgramMapper malariaProgramMapper;

    @Autowired
    private ImplementationMapper implementationMapper;
    private Treatment treatment;
    private Implementation implementation;
    private Product product;

    @Before
    public void setUp() throws Exception {
        MalariaProgram program = make(a(randomMalariaProgram));
        malariaProgramMapper.insert(program);
        implementation = make(a(randomImplementation, with(malariaProgram, program)));
        product = make(a(randomProduct));
        treatment = make(a(randomTreatment,
                with(TreatmentBuilder.implementation, implementation),
                with(TreatmentBuilder.product, product)));
    }

    @Test
    public void shouldInsert() throws Exception {
        productMapper.insert(product);
        implementationMapper.insert(implementation);
        int count = mapper.insert(treatment);
        assertThat(count, is(not(0)));
        assertThat(treatment.getId(), is(not(0)));
    }

    @Test
    public void shouldBulkInsert() throws Exception {
        productMapper.insert(product);
        implementationMapper.insert(implementation);
        List<Treatment> treatments = createRandomTreatments();
        int count = mapper.bulkInsert(treatments);
        assertThat(count, is((treatments.size())));
        for (Treatment treatment : treatments) {
            assertThat(treatment.getId(), is(not(0)));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenProductDoesNotExist() {
        implementationMapper.insert(implementation);
        mapper.insert(treatment);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertWhenImplementationDoesNotExist() {
        productMapper.insert(product);
        mapper.insert(treatment);
    }

    private List<Treatment> createRandomTreatments() {
        List<Treatment> result = new ArrayList<>();
        int randomQuantity = nextInt(10) + 1;
        for (int i = 0; i < randomQuantity; i++) {
            result.add(make(a(randomTreatment,
                    with(TreatmentBuilder.implementation, implementation),
                    with(TreatmentBuilder.product, product))));
        }
        return result;
    }
}
