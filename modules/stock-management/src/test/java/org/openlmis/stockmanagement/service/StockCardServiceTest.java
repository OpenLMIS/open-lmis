package org.openlmis.stockmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.openlmis.stockmanagement.repository.StockCardRepository;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class StockCardServiceTest {

    @Mock
    private FacilityService facilityService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private StockCardRepository repository;

    private StockCardService service;

    private static final Facility defaultFacility;
    private static final Product defaultProduct;
    private static final StockCard dummyCard;

    private long stockCardId;
    private Lot lot;
    private Lot createdLot;
    private LotOnHand expectedLotOnHand;

    static  {
        defaultFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, 1L)));
        defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "CODE")));
        dummyCard = StockCard.createZeroedStockCard(defaultFacility, defaultProduct);
    }

    @Before
    public void setup() {
        stockCardId = 1;
        dummyCard.setId(stockCardId);

        lot = new Lot();
        lot.setProduct(defaultProduct);
        lot.setLotCode("A1");
        lot.setManufacturerName("Manu");
        lot.setManufactureDate(new Date());
        lot.setExpirationDate(new Date());

        createdLot = new Lot();
        createdLot.setProduct(lot.getProduct());
        createdLot.setLotCode(lot.getLotCode());
        createdLot.setManufacturerName(lot.getManufacturerName());
        createdLot.setManufactureDate(lot.getManufactureDate());
        createdLot.setExpirationDate(lot.getExpirationDate());

        expectedLotOnHand = LotOnHand.createZeroedLotOnHand(lot, dummyCard);

        service = new StockCardService(facilityService,
                productRepository,
                lotRepository,
                repository);
    }

    @Test
    public void shouldReturnExistingLot() {
        when(lotRepository.getLotOnHandByStockCardAndLotObject(stockCardId, lot)).thenReturn(expectedLotOnHand);
        LotOnHand lotOnHand = service.getOrCreateLotOnHand(lot, dummyCard);

        assertEquals(lotOnHand.getQuantityOnHand(), expectedLotOnHand.getQuantityOnHand());
        assertEquals(lotOnHand.getLot().getLotCode(), expectedLotOnHand.getLot().getLotCode());
    }

    @Test
    public void shouldCreateNonExistingLot() {
        when(lotRepository.getLotOnHandByStockCardAndLotObject(stockCardId, lot)).thenReturn(null);
        when(lotRepository.getOrCreateLot(lot)).thenReturn(createdLot);
        LotOnHand lotOnHand = service.getOrCreateLotOnHand(lot, dummyCard);

        assertEquals(lotOnHand.getQuantityOnHand(), expectedLotOnHand.getQuantityOnHand());
        assertEquals(lotOnHand.getLot().getLotCode(), expectedLotOnHand.getLot().getLotCode());
    }

    @Test
    public void shouldReturnNullLotOnHandWithNoLotInfo() {
        StringBuilder str = new StringBuilder();

        LotOnHand lotOnHand = service.getLotOnHand(null, null, defaultProduct.getCode(), dummyCard, str);

        // verify
        assertNull(lotOnHand);
    }

    @Test
    public void shouldErrorWithInvalidLotId() {
        long lotId = 1;
        StringBuilder str = new StringBuilder();

        when(lotRepository.getLotOnHandByStockCardAndLot(dummyCard.getId(), lotId)).thenReturn(null);
        LotOnHand lotOnHand = service.getLotOnHand(lotId, null, defaultProduct.getCode(), dummyCard, str);

        // verify
        assertNull(lotOnHand);
        assertEquals(str.toString(), "error.lot.unknown");
    }

    @Test
    public void shouldGetLotOnHandWithValidLotId() {
        long lotId = 1;
        StringBuilder str = new StringBuilder();

        // test
        when(lotRepository.getLotOnHandByStockCardAndLot(dummyCard.getId(), lotId)).thenReturn(expectedLotOnHand);
        LotOnHand lotOnHand = service.getLotOnHand(lotId, null, defaultProduct.getCode(), dummyCard, str);

        // verify
        assertEquals(expectedLotOnHand, lotOnHand);
    }

    @Test
    public void shouldErrorWithInvalidLotObject() {
        StringBuilder str = new StringBuilder();
        lot.setLotCode("");

        // test
        LotOnHand lotOnHand = service.getLotOnHand(null, lot, defaultProduct.getCode(), dummyCard, str);

        // verify
        assertNull(lotOnHand);
        assertEquals(str.toString(), "error.lot.invalid");
    }

//    @Test
//    public void shouldErrorWithStockCardAndLotNotFound() {
//        StringBuilder str = new StringBuilder();
//
//        // test
//        when(service.getOrCreateLotOnHand(lot, dummyCard)).thenReturn(null);
//        LotOnHand lotOnHand = service.getLotOnHand(null, lot, defaultProduct.getId(), dummyCard, str);
//
//        // verify
//        assertNull(lotOnHand);
//    }

    @Test
    public void shouldSucceedWithValidLotObject() {
        StringBuilder str = new StringBuilder();

        // test
        when(lotRepository.getLotOnHandByStockCardAndLotObject(dummyCard.getId(), lot)).thenReturn(expectedLotOnHand);
        when(service.getOrCreateLotOnHand(lot, dummyCard)).thenReturn(expectedLotOnHand);
        LotOnHand lotOnHand = service.getLotOnHand(null, lot, defaultProduct.getCode(), dummyCard, str);

        // verify
        assertEquals(expectedLotOnHand, lotOnHand);
    }
}
