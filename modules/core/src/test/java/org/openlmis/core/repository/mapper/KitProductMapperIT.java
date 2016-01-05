package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Kit;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class KitProductMapperIT {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private KitProductMapper kitProductMapper;

    @Test
    public void shouldInsertKitProduct() {
        Kit kit = new Kit();
        kit.setCode("Kit 1");
        kit.setPrimaryName("Kit 1 name");
        kit.setActive(true);

        productMapper.insert(kit);

        Product product = make(a(defaultProduct));
        product.setQuantityInKit(100);
        productMapper.insert(product);

        KitProduct kitProduct = new KitProduct(kit, product, 100);
        kitProductMapper.insert(kitProduct);

        List<KitProduct> kitProducts = kitProductMapper.getByKit(kit);

        assertThat(kitProducts.size(), is(1));
        assertThat(kitProducts.get(0).getQuantity(), is(100));
    }
}
