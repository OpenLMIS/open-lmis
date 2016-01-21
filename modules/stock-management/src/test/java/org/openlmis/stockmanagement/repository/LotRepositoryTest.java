package org.openlmis.stockmanagement.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.mapper.LotMapper;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class LotRepositoryTest {

    @Mock
    LotMapper mapper;

    LotRepository repository;

    private static final Product defaultProduct;

    private Lot lot;

    static  {
        defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.productId, 1L)));
    }

    @Before
    public void setup() {
        lot = new Lot();
        lot.setProduct(defaultProduct);
        lot.setLotCode("A1");
        lot.setManufacturerName("Manu");
        lot.setManufactureDate(new Date());
        lot.setExpirationDate(new Date());

        repository = new LotRepository(mapper);
    }

    @Test
    public void shouldGetExistingLot() {
        when(mapper.getByObject(lot)).thenReturn(lot);

        Lot l = repository.getOrCreateLot(lot);
        assertEquals(l.getLotCode(), lot.getLotCode());
        assertEquals(l.getManufacturerName(), lot.getManufacturerName());
        assertEquals(l.getExpirationDate(), lot.getExpirationDate());
    }

    @Test
    public void shouldCreateNonExistingLot() {
        when(mapper.getByObject(lot)).thenReturn(null);

        Lot l = repository.getOrCreateLot(lot);
        verify(mapper).insert(lot);
        assertEquals(l.getLotCode(), lot.getLotCode());
        assertEquals(l.getManufacturerName(), lot.getManufacturerName());
        assertEquals(l.getExpirationDate(), lot.getExpirationDate());
    }
}
