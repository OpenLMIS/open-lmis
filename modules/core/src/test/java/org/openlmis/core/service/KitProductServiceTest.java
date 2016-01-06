package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Kit;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.KitProductRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class KitProductServiceTest {

    @InjectMocks
    KitProductService kitProductService;

    @Mock
    private KitProductRepository repository;

    @Test
    public void shouldInsertOneKit() throws Exception {
        KitProduct kitProduct = new KitProduct();
        kitProductService.insert(kitProduct);

        verify(repository).insert(kitProduct);
    }

    @Test
    public void shouldGetLatestProductsForKitWhenUpdatedTimeExists() throws Exception {
        Date date = new Date(1234567L);
        Long kitId = 100L;
        List<KitProduct> kitProductList = Arrays.asList(
                new KitProduct(new Kit(), new Product(), 100),
                new KitProduct(new Kit(), new Product(), 200),
                new KitProduct(new Kit(), new Product(), 200)
        );
        when(repository.getLatestKitProductByKitId(kitId, date)).thenReturn(kitProductList);

        List<Product> productsForKit = kitProductService.getProductsForKitAfterUpdatedTime(kitId, date);

        verify(repository).getLatestKitProductByKitId(kitId, date);
        assertEquals(3, productsForKit.size());
        assertEquals(Integer.valueOf(200), productsForKit.get(1).getQuantityInKit());
    }

    @Test
    public void shouldGetAllProductsForKitWhenUpdatedTimeIsEmpty() throws Exception {
        Long kitId = 100L;
        List<KitProduct> kitProductList = Arrays.asList(
                new KitProduct(new Kit(), new Product(), 100),
                new KitProduct(new Kit(), new Product(), 200),
                new KitProduct(new Kit(), new Product(), 200)
        );
        when(repository.getByKitId(kitId)).thenReturn(kitProductList);

        List<Product> productsForKit = kitProductService.getProductsForKitAfterUpdatedTime(kitId, null);

        verify(repository).getByKitId(kitId);
        assertEquals(3, productsForKit.size());
        assertEquals(Integer.valueOf(200), productsForKit.get(1).getQuantityInKit());
    }
}