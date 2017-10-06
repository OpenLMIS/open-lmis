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
import org.openlmis.programs.helpers.ImplementationBuilder;
import org.openlmis.programs.helpers.MalariaProgramBuilder;
import org.openlmis.programs.helpers.ProductBuilder;
import org.openlmis.programs.helpers.TreatmentBuilder;
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
    private MalariaProgramMapper malariaProgramMapper;

    @Autowired
    private ImplementationMapper implementationMapper;
    private Treatment treatment;
    private Implementation implementation;
    private Product product;

    @Before
    public void setUp() throws Exception {
        MalariaProgram malariaProgram = MalariaProgramBuilder.fresh().build();
        malariaProgramMapper.insert(malariaProgram);
        implementation = ImplementationBuilder.fresh().setMalariaProgram(malariaProgram).build();
        product = ProductBuilder.fresh().build();
        treatment = TreatmentBuilder.fresh()
                .setImplementation(implementation)
                .setProduct(product)
                .build();
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
            result.add(TreatmentBuilder.fresh().setImplementation(implementation).setProduct(product).build());
        }
        return result;
    }
}
