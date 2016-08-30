package org.openlmis.stockmanagement.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.repository.mapper.LotMapper;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class StockCardRepositoryTest {

  @Mock
  StockCardMapper mapper;

  @Mock
  LotMapper lotMapper;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private ProductRepository productRepository;

  private static final Facility defaultFacility;
  private static final Product defaultProduct;

  @InjectMocks
  private StockCardRepository stockCardRepository;

  static {
    defaultFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, 1L)));
    defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "CODE")));
  }


  @Test
  public void shouldGetStockCardIfItExists() {
    StockCard dummyCard = StockCard.createZeroedStockCard(defaultFacility, defaultProduct);
    when(mapper.getByFacilityAndProduct(defaultFacility.getId(), defaultProduct.getCode())).thenReturn(dummyCard);

    StockCard stockCard = stockCardRepository.getOrCreateStockCard(defaultFacility.getId(), defaultProduct.getCode(), 123L);
    assertThat(stockCard, is(dummyCard));
  }

  @Test
  public void shouldCreateStockCardIfItDoesNotExist() {
    when(mapper.getByFacilityAndProduct(defaultFacility.getId(), defaultProduct.getCode())).thenReturn(null);
    when(facilityRepository.getById(defaultFacility.getId())).thenReturn(defaultFacility);
    when(productRepository.getByCode(defaultProduct.getCode())).thenReturn(defaultProduct);

    StockCard stockCard = stockCardRepository.getOrCreateStockCard(defaultFacility.getId(), defaultProduct.getCode(), 123L);
    verify(mapper).insert(stockCard);
    assertThat(stockCard.getFacility(), is(defaultFacility));
    assertThat(stockCard.getProduct(), is(defaultProduct));
    assertThat(stockCard.getCreatedBy(), is(123L));
  }

  @Test
  public void shouldGetStockCardWithLatestEntries() {
    StockCard dummyCard = StockCard.createZeroedStockCard(defaultFacility, defaultProduct);
    Date startDate = new Date();
    Date endDate = new Date();
    Long facilityId = defaultFacility.getId();
    when(mapper.queryStockCardBasicInfo(facilityId)).thenReturn(asList(dummyCard));

    StockCardEntry entry = new StockCardEntry();
    when(mapper.queryStockCardEntriesByDateRange(dummyCard.getId(), startDate, endDate)).thenReturn(asList(entry));

    List<StockCard> stockCards = stockCardRepository.queryStockCardByOccurred(facilityId, startDate, endDate);
    assertThat(stockCards.size(), is(1));
  }

  @Test
  public void shouldCallMapperUpdateAllStockCardSyncTimeForFacility() {
    stockCardRepository.updateAllStockCardSyncTimeForFacility(123L);
    verify(mapper).updateAllStockCardSyncTimeForFacilityToNow(123L);
  }

  @Test
  public void shouldCallMapperUpdateStockCardSyncTimeToNow() {
    String stockCardProductCode = "123";
    stockCardRepository.updateStockCardSyncTimeToNow(123L, stockCardProductCode);
    verify(mapper).updateStockCardToSyncTimeToNow(123L, stockCardProductCode);
  }

  @Test
  public void shouldPersistLotMovementsIfStockEntryContainsThem() {
    StockCard stockCard = new StockCard();
    stockCard.setFacility(defaultFacility);
    stockCard.setProduct(defaultProduct);
    StockCardEntry stockCardEntry = new StockCardEntry(stockCard, StockCardEntryType.CREDIT, 100L, new Date(), "", 0L);

    Lot lot = new Lot();
    lot.setProduct(defaultProduct);
    lot.setLotCode("AAA");
    lot.setExpirationDate(new Date());
    lot.setId(1L);
    LotOnHand lotOnHand = new LotOnHand();
    lotOnHand.setLot(lot);
    lotOnHand.setQuantityOnHand(100L);
    lotOnHand.setStockCard(stockCard);
    lotOnHand.setId(1L);
    StockCardEntryLotItem stockCardEntryLotItem1 = new StockCardEntryLotItem(lot, 10L);
    stockCardEntryLotItem1.addKeyValue("SOH", "100");

    Lot lot2 = new Lot();
    lot2.setProduct(defaultProduct);
    lot2.setLotCode("BBB");
    lot2.setExpirationDate(new Date());
    LotOnHand lotOnHand2 = new LotOnHand();
    lotOnHand2.setLot(lot2);
    lotOnHand2.setQuantityOnHand(100L);
    lotOnHand2.setStockCard(stockCard);
    StockCardEntryLotItem stockCardEntryLotItem2 = new StockCardEntryLotItem(lot2, 20L);
    stockCardEntryLotItem2.addKeyValue("SOH", "200");

    stockCard.setLotsOnHand(asList(lotOnHand, lotOnHand2));
    stockCardEntry.setStockCardEntryLotItems(asList(stockCardEntryLotItem1, stockCardEntryLotItem2));
    stockCardEntry.getStockCard().setLotsOnHand(asList(lotOnHand, lotOnHand2));

    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        ((StockCardEntry) invocation.getArguments()[0]).setId(123L);
        return null;
      }
    }).when(mapper).insertEntry(stockCardEntry);

    stockCardRepository.persistStockCardEntry(stockCardEntry);

    ArgumentCaptor<StockCardEntryLotItem> captor = ArgumentCaptor.forClass(StockCardEntryLotItem.class);

    verify(mapper).insertEntry(stockCardEntry);

    verify(lotMapper, times(2)).insertStockCardEntryLotItem(captor.capture());
    assertEquals(123L, captor.getAllValues().get(0).getStockCardEntryId(), 0L);
    assertEquals(123L, captor.getAllValues().get(1).getStockCardEntryId(), 0L);

    ArgumentCaptor<StockCardEntryLotItemKV> captor2 = ArgumentCaptor.forClass(StockCardEntryLotItemKV.class);
    verify(lotMapper, times(2)).insertStockCardEntryLotItemKV(Matchers.any(StockCardEntryLotItem.class), captor2.capture());
    assertEquals("100", captor2.getAllValues().get(0).getValue());
    assertEquals("200", captor2.getAllValues().get(1).getValue());
  }

  @Test
  public void shouldUpdateStockCardWithLotsOnHand() throws Exception {
    StockCard stockCard = new StockCard();
    stockCard.setFacility(defaultFacility);
    stockCard.setProduct(defaultProduct);
    Lot lot = new Lot();
    lot.setProduct(defaultProduct);
    lot.setLotCode("AAA");
    lot.setExpirationDate(new Date());
    lot.setId(1L);
    LotOnHand lotOnHand = new LotOnHand();
    lotOnHand.setLot(lot);
    lotOnHand.setQuantityOnHand(100L);
    lotOnHand.setStockCard(stockCard);
    lotOnHand.setId(1L);

    Lot lot2 = new Lot();
    lot2.setProduct(defaultProduct);
    lot2.setLotCode("BBB");
    lot2.setExpirationDate(new Date());
    LotOnHand lotOnHand2 = new LotOnHand();
    lotOnHand2.setLot(lot2);
    lotOnHand2.setQuantityOnHand(100L);
    lotOnHand2.setStockCard(stockCard);
    stockCard.setLotsOnHand(asList(lotOnHand, lotOnHand2));

    stockCardRepository.updateStockCard(stockCard);

    verify(lotMapper, never()).insert(lot);
    verify(lotMapper).insert(lot2);
    verify(lotMapper).updateLotOnHand(lotOnHand);
    verify(lotMapper).insertLotOnHand(lotOnHand2);
  }
}