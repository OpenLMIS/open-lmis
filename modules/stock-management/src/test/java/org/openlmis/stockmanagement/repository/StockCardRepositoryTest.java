package org.openlmis.stockmanagement.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class StockCardRepositoryTest {

  @Mock
  StockCardMapper mapper;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private ProductRepository productRepository;

  private static final Facility defaultFacility;
  private static final Product defaultProduct;

  @InjectMocks
  private StockCardRepository stockCardRepository;

  static  {
    defaultFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, 1L)));
    defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "CODE")));
  }



  @Test
  public void shouldGetStockCardIfItExists() {
    StockCard dummyCard = StockCard.createZeroedStockCard(defaultFacility, defaultProduct);
    when(mapper.getByFacilityAndProduct(defaultFacility.getId(), defaultProduct.getCode())).thenReturn(dummyCard);

    StockCard stockCard = stockCardRepository.getOrCreateStockCard(defaultFacility.getId(), defaultProduct.getCode());
    assertThat(stockCard, is(dummyCard));
  }

  @Test
  public void shouldCreateStockCardIfItDoesNotExist() {
    when(mapper.getByFacilityAndProduct(defaultFacility.getId(), defaultProduct.getCode())).thenReturn(null);
    when(facilityRepository.getById(defaultFacility.getId())).thenReturn(defaultFacility);
    when(productRepository.getByCode(defaultProduct.getCode())).thenReturn(defaultProduct);

    StockCard stockCard = stockCardRepository.getOrCreateStockCard(defaultFacility.getId(), defaultProduct.getCode());
    verify(mapper).insert(stockCard);
    assertThat(stockCard.getFacility(), is(defaultFacility));
    assertThat(stockCard.getProduct(), is(defaultProduct));
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
    verify(mapper).updateStockCardToSyncTimeToNow(123L,stockCardProductCode);
  }
}