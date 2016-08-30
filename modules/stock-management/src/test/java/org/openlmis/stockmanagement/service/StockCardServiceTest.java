package org.openlmis.stockmanagement.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

  static {
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

  @Test
  public void shouldCallRepositoryUpdateAllStockCardSyncTimeForFacility() {
    service.updateAllStockCardSyncTimeForFacilityToNow(123L);
    verify(repository).updateAllStockCardSyncTimeForFacility(123L);
  }

  @Test
  public void shouldCallRepositoryUpdateStockCardSyncTimeToNowForProductCodesNotInList() {

    StockCard stockCard1 = new StockCard();
    stockCard1.setProduct(make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P1"))));
    StockCard stockCard2 = new StockCard();
    stockCard2.setProduct(make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P2"))));
    when(repository.getStockCards(123L)).thenReturn(asList(stockCard1, stockCard2));

    service.updateStockCardSyncTimeToNow(123L, asList("P1"));
    verify(repository, never()).updateStockCardSyncTimeToNow(123L, "P1");
    verify(repository).updateStockCardSyncTimeToNow(123L, "P2");
  }

  @Test
  public void shouldReturnLotOnHandByLotNumberAndProductCode() throws Exception {
    StockCard stockCard1 = new StockCard();
    stockCard1.setProduct(make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P1"))));
    stockCard1.setFacility(defaultFacility);

    service.getLotOnHandByLotNumberAndProductCodeAndFacilityId(lot.getLotCode(), stockCard1.getProduct().getCode(), stockCard1.getFacility().getId());
    verify(lotRepository).getLotOnHandByLotNumberAndProductCodeAndFacilityId(lot.getLotCode(), stockCard1.getProduct().getCode(), stockCard1.getFacility().getId());
  }

  @Test
  public void shouldSaveLotMovementsAndUpdateLotOnHandWhenStockEntryHasLotMovements() throws Exception {
    StockCard stockCard = new StockCard();
    stockCard.setFacility(defaultFacility);
    stockCard.setProduct(defaultProduct);
    stockCard.setTotalQuantityOnHand(100L);
    StockCardEntry stockCardEntry = new StockCardEntry(stockCard, StockCardEntryType.CREDIT, 100L, new Date(), "", 0L);

    Lot lot = new Lot();
    lot.setProduct(defaultProduct);
    lot.setLotCode("AAA");
    lot.setExpirationDate(new Date());
    LotOnHand lotOnHand = new LotOnHand();
    lotOnHand.setLot(lot);
    lotOnHand.setQuantityOnHand(100L);
    lotOnHand.setStockCard(stockCard);

    Lot lot2 = new Lot();
    lot2.setProduct(defaultProduct);
    lot2.setLotCode("BBB");
    lot2.setExpirationDate(new Date());
    LotOnHand lotOnHand2 = new LotOnHand();
    lotOnHand2.setLot(lot2);
    lotOnHand2.setQuantityOnHand(0L);
    lotOnHand2.setStockCard(stockCard);

    StockCardEntryLotItem stockCardEntryLotItem1 = new StockCardEntryLotItem(lot, 10L);
    StockCardEntryLotItem stockCardEntryLotItem2 = new StockCardEntryLotItem(lot2, 100L);
    StockCardEntryLotItem stockCardEntryLotItem3 = new StockCardEntryLotItem(lot, -30L);

    stockCard.setLotsOnHand(asList(lotOnHand, lotOnHand2));
    stockCardEntry.setStockCardEntryLotItems(asList(stockCardEntryLotItem1, stockCardEntryLotItem2, stockCardEntryLotItem3));

    service.addStockCardEntry(stockCardEntry);

    verify(lotRepository).createStockCardEntryLotItem(stockCardEntryLotItem1);
    verify(lotRepository).createStockCardEntryLotItem(stockCardEntryLotItem2);
    verify(lotRepository).createStockCardEntryLotItem(stockCardEntryLotItem3);

    ArgumentCaptor<LotOnHand> lotOnHandArgumentCaptor = ArgumentCaptor.forClass(LotOnHand.class);
    verify(lotRepository, times(2)).saveLotOnHand(lotOnHandArgumentCaptor.capture());
    List<LotOnHand> allValues = lotOnHandArgumentCaptor.getAllValues();
    if (allValues.get(0).getLot().getLotCode().equals("AAA")) {
      assertThat(allValues.get(0).getQuantityOnHand(), is(80L));
      assertThat(allValues.get(1).getQuantityOnHand(), is(100L));
    } else {
      assertThat(allValues.get(0).getQuantityOnHand(), is(100L));
      assertThat(allValues.get(1).getQuantityOnHand(), is(80L));
    }
  }
}
