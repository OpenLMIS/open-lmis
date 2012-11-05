package org.openlmis.core.dao;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @Test
    public void shouldInsertProductIntoDatabase() throws Exception {
    }
}
