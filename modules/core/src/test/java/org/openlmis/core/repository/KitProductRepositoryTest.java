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

import java.util.Date;

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

    @Test
    public void shouldGetLatestKitProductsByKitId() throws Exception {
        Long kitId = 100L;
        Date afterUpdatedTime = new Date(1223444L);

        kitProductRepository.getLatestKitProductByKitId(kitId, afterUpdatedTime);

        verify(mapper).getLatestKitProductByKitId(kitId, afterUpdatedTime);
    }

    @Test
    public void shouldGetAllKitsByKitId() throws Exception {
        Long kitId = 100L;
        kitProductRepository.getByKitId(kitId);

        verify(mapper).getByKitId(kitId);
    }
}