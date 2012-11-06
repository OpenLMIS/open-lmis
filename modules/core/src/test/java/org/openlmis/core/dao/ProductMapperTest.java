package org.openlmis.core.dao;


import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProductMapperTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Autowired
    ProductMapper productMapper;

    @Test
    public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
        expectedEx.expect(DataIntegrityViolationException.class);
        expectedEx.expectMessage("null value in column \"primary_name\" violates not-null constraint");
        Product product = new Product();
        product.setCode("ABCD123");
        int count = productMapper.insert(product);
        assertEquals(0, count);
    }

    @Test
    @Ignore
    public void testShouldSaveProductIfAllMandatoryFieldsArePresent() throws Exception {
        Product product = new Product();
        product.setCode("ABCD123");
        product.setAlternateItemCode("alternate-1231");
//        product.setm
    }
}
