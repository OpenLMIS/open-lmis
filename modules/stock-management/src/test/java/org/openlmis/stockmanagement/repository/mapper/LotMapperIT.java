package org.openlmis.stockmanagement.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.domain.LotOnHand;
import org.openlmis.stockmanagement.domain.StockCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-stock-management.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class LotMapperIT {
    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockCardMapper stockCardMapper;

    @Autowired
    private LotMapper lotMapper;

    private StockCard defaultCard;
    private Facility defaultFacility;
    private Product defaultProduct;
    private Lot defaultLot;
    private LotOnHand defaultLotOnHand;

    @Before
    public void setup() {
        defaultFacility = make(a(FacilityBuilder.defaultFacility));
        defaultProduct = make(a(ProductBuilder.defaultProduct));
        facilityMapper.insert(defaultFacility);
        productMapper.insert(defaultProduct);

        defaultCard = new StockCard();
        defaultCard.setFacility(defaultFacility);
        defaultCard.setProduct(defaultProduct);
        defaultCard.setTotalQuantityOnHand(0L);

        stockCardMapper.insert(defaultCard);

        defaultLot = new Lot();
        defaultLot.setLotCode("TEST");
        defaultLot.setExpirationDate(new Date());
        defaultLot.setProduct(defaultProduct);
        lotMapper.insert(defaultLot);

        defaultLotOnHand = new LotOnHand();
        defaultLotOnHand.setLot(defaultLot);
        defaultLotOnHand.setStockCard(defaultCard);
        defaultLotOnHand.setQuantityOnHand(100L);

        lotMapper.insertLotOnHand(defaultLotOnHand);
    }

    @Test
    public void shouldGetLotByLotNumberAndProductCodeAndFacilityId() throws Exception {
        LotOnHand actualLotOnHand = lotMapper.getLotOnHandByLotNumberAndProductCodeAndFacilityId(defaultLot.getLotCode(), defaultProduct.getCode(), defaultFacility.getId());
        assertEquals("TEST", actualLotOnHand.getLot().getLotCode());
        assertEquals(100L, actualLotOnHand.getQuantityOnHand(), 0L);
    }
}