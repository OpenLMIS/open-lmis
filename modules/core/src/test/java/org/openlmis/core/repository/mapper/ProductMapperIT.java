package org.openlmis.core.repository.mapper;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProductMapperIT {

    public static final String PRODUCT_DOSAGE_UNIT_MG = "mg";
    private static final String PRODUCT_FORM_TABLET = "Tablet";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Autowired
    ProgramProductMapper programProductMapper;
    @Autowired
    FacilityMapper facilityMapper;
    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    ProductMapper productMapper;

    @Test
    public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
        expectedEx.expect(DataIntegrityViolationException.class);
        expectedEx.expectMessage("null value in column \"primary_name\" violates not-null constraint");
        Product product = new Product();
        product.setCode("ABCD123");
        int status = productMapper.insert(product);
        assertEquals(0, status);
    }

    @Test
    public void shouldReturnDosageUnitIdForCode() {
        Integer id = productMapper.getDosageUnitIdForCode(PRODUCT_DOSAGE_UNIT_MG);
        assertThat(id, CoreMatchers.is(1));

        id = productMapper.getDosageUnitIdForCode("invalid dosage unit");
        assertThat(id, CoreMatchers.is(nullValue()));
    }

    @Test
    public void shouldReturnProductFormIdForCode() {
        Integer id = productMapper.getProductFormIdForCode(PRODUCT_FORM_TABLET);
        assertThat(id, CoreMatchers.is(1));

        id = productMapper.getProductFormIdForCode("invalid product form");
        assertThat(id, CoreMatchers.is(nullValue()));
    }
}
