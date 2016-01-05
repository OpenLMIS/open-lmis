package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Kit;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.KitProductMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class KitProductRepositoryTest {
    @Mock
    KitProductMapper mapper;

    @InjectMocks
    KitProductRepository kitProductRepository;


    @Test
    public void shouldInsertKitProduct() throws Exception {
        KitProduct kitProduct = new KitProduct(new Kit(), new Product(), 100);
        kitProductRepository.insert(kitProduct);

        verify(mapper).insert(kitProduct);
    }
}