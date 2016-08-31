package org.openlmis.stockmanagement.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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

  @Autowired
  QueryExecutor queryExecutor;

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

  @Test
  public void shouldUpdateLotOnHand() throws Exception {
    LotOnHand existingLotOnHand = lotMapper.getLotOnHandByLotNumberAndProductCodeAndFacilityId(defaultLot.getLotCode(), defaultProduct.getCode(), defaultFacility.getId());
    existingLotOnHand.setQuantityOnHand(99L);
    lotMapper.updateLotOnHand(existingLotOnHand);

    LotOnHand updatedLotOnHand = lotMapper.getLotOnHandByLotNumberAndProductCodeAndFacilityId(defaultLot.getLotCode(), defaultProduct.getCode(), defaultFacility.getId());
    assertEquals(99L, updatedLotOnHand.getQuantityOnHand(), 0L);
  }

  @Test
  public void shouldInsertLotMovementItem() throws Exception {
    StockCardEntryLotItem stockCardEntryLotItem = new StockCardEntryLotItem(defaultLot, 5L);
    stockCardEntryLotItem.addKeyValue("SOH", "500");
    StockCardEntry stockCardEntry = new StockCardEntry(defaultCard, StockCardEntryType.ADJUSTMENT, 10L, new Date(), "", null);
    stockCardMapper.insertEntry(stockCardEntry);
    stockCardEntryLotItem.setStockCardEntryId(stockCardEntry.getId());
    lotMapper.insertStockCardEntryLotItem(stockCardEntryLotItem);
    assertThat(stockCardEntryLotItem.getQuantity(), is(lotMapper.getLotMovementItemsByStockEntry(stockCardEntry.getId()).get(0).getQuantity()));
    assertThat(stockCardEntryLotItem.getExtensions().get(0).getValue(), is("500"));
  }

  @Test
  public void shouldInsertLotMovementItemKV() throws Exception {
    StockCardEntryLotItem stockCardEntryLotItem = new StockCardEntryLotItem(defaultLot, 5L);
    stockCardEntryLotItem.addKeyValue("SOH", "500");
    StockCardEntry stockCardEntry = new StockCardEntry(defaultCard, StockCardEntryType.ADJUSTMENT, 10L, new Date(), "", null);
    stockCardMapper.insertEntry(stockCardEntry);
    stockCardEntryLotItem.setStockCardEntryId(stockCardEntry.getId());
    lotMapper.insertStockCardEntryLotItem(stockCardEntryLotItem);
    lotMapper.insertStockCardEntryLotItemKV(stockCardEntryLotItem, stockCardEntryLotItem.getExtensions().get(0));
    assertEquals(stockCardEntryLotItem.getExtensions().get(0).getValue(), lotMapper.getStockCardEntryLotItemExtensions(stockCardEntryLotItem.getId()).get(0).getValue());
  }

  @Test
  public void shouldGetLotById() throws Exception {
    Lot lotResult = lotMapper.getById(defaultLot.getId());
    assertThat(lotResult.getLotCode(), is("TEST"));
    assertThat(lotResult.getProduct().getCode(), is(defaultProduct.getCode()));
    assertNotNull(lotResult.getExpirationDate());
  }

  @Test
  public void shouldGetLotByCodeAndProductId() throws Exception {
    Lot lotResult = lotMapper.getLotByLotNumberAndProductId(defaultLot.getLotCode(), defaultLot.getProduct().getId());
    assertThat(lotResult.getLotCode(), is("TEST"));
    assertThat(lotResult.getProduct().getCode(), is(defaultProduct.getCode()));
    assertNotNull(lotResult.getExpirationDate());
  }

  @Test
  public void shouldGetLotOnHandByLotAndStockCard() throws Exception {
    LotOnHand lotOnHand = lotMapper.getLotOnHandByStockCardAndLot(defaultCard.getId(), defaultLot.getId());

    assertThat(lotOnHand.getQuantityOnHand(), is(100L));
    assertThat(lotOnHand.getLot().getLotCode(), is(defaultLot.getLotCode()));
  }

  @Test
  public void shouldInsertLotConflictButNotInsertDuplicate() throws Exception {
    Date expirationDate = DateUtil.parseDate("2020-10-30", DateUtil.FORMAT_DATE);
    lotMapper.insertLotConflict(defaultLot.getId(), expirationDate, 1L);
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM lot_conflicts WHERE lotid = " + defaultLot.getId());
    resultSet.next();
    assertThat(resultSet.getDate("expirationdate"), is(expirationDate));
    assertThat(resultSet.getLong("createdby"), is(1L));
    assertNotNull(resultSet.getDate("createddate"));

    lotMapper.insertLotConflict(defaultLot.getId(), expirationDate, 1L);
    resultSet = queryExecutor.execute("SELECT count(*) FROM lot_conflicts WHERE lotid = " + defaultLot.getId());
    resultSet.next();
    assertThat(resultSet.getInt("count"), is(1));
  }
}