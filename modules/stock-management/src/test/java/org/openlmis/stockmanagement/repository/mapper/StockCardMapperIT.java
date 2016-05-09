/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.domain.StockCardEntryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.openlmis.core.builder.ProductBuilder.active;
import static org.openlmis.core.builder.ProductBuilder.code;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-stock-management.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class StockCardMapperIT {

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private StockCardMapper mapper;

  @Autowired
  private StockAdjustmentReasonMapper stockAdjustmentReasonMapper;

  private StockCard defaultCard;
  private Facility defaultFacility;

  private Product defaultProduct;
  @Autowired
  private QueryExecutor queryExecutor;
  private StockCard stockCard1;
  private StockCard stockCard2;

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

    mapper.insert(defaultCard);
  }

  @Test
  public void shouldInsertEntry() {
    StockCardEntry entry = getStockCardEntry();
    Timestamp date = new Timestamp(DateUtil.parseDate("2015-12-12 12:12:12").getTime());
    entry.setCreatedDate(date);
    mapper.insertEntry(entry);

    List<StockCardEntry> entries = mapper.getEntries(defaultCard.getId());
    assertThat(entries.size(), is(1));
    assertThat(DateUtil.formatDate(entries.get(0).getCreatedDate()), is("2015-12-12 12:12:12"));

  }

  private StockCardEntry getStockCardEntry() {
    StockCardEntry entry = new StockCardEntry(defaultCard, StockCardEntryType.CREDIT, 1L, null, null);
    entry.setCreatedDate(new Date());
    return entry;
  }

  @Test
  public void shouldInsertEntryKeyValues() {
    StockCardEntry entry = getStockCardEntry();
    mapper.insertEntry(entry);
    mapper.insertEntryKeyValue(entry, "vvmstatus", "1");

    List<StockCardEntry> entries = mapper.getEntries(defaultCard.getId());

    for (StockCardEntryKV kv : entries.get(0).getExtensions()) {
      if (kv.getKey().equalsIgnoreCase("vvmstatus")) {
        assertEquals(kv.getValue(), "1");
      }
    }
  }

  @Test
  public void shouldGetStockCardByFacilityIdAndProductCode() {
    StockCardEntry entry = getStockCardEntry();
    StockAdjustmentReason reason = StockAdjustmentReason.create("reason");
    stockAdjustmentReasonMapper.insert(reason);
    entry.setAdjustmentReason(reason);
    mapper.insertEntry(entry);

    StockCard stockCard = mapper.getByFacilityAndProduct(defaultFacility.getId(), defaultProduct.getCode());
    assertThat(stockCard.getProduct().getCode(), is(defaultProduct.getCode()));
    assertThat(stockCard.getFacility().getId(), is(defaultFacility.getId()));
    assertThat(stockCard.getEntries().get(0).getAdjustmentReason().getName(), is("reason"));
  }

  @Test
  public void shouldSaveStockEntryOccurred() {
    Date occurred = DateUtil.parseDate("2015-10-30 00:00:00");
    StockCardEntry entry = getStockCardEntry();
    entry.setOccurred(occurred);
    mapper.insertEntry(entry);

    List<StockCardEntry> entries = mapper.getEntries(defaultCard.getId());

    assertThat(entries.get(0).getOccurred(), is(occurred));
  }

  @Test
  public void shouldSaveStockEntryDocumentNumber() {
    String referenceNumber = "110";
    StockCardEntry entry = getStockCardEntry();
    entry.setReferenceNumber(referenceNumber);
    mapper.insertEntry(entry);

    List<StockCardEntry> entries = mapper.getEntries(defaultCard.getId());

    assertThat(entries.get(0).getReferenceNumber(), is(referenceNumber));
  }

  @Test
  public void shouldReturnStockCardBasicInfoWhenGiveFacilityId() throws Exception {

    StockCardEntry entry = getStockCardEntry();
    String expirationDate = "2015/1/1";
    mapper.insertEntry(entry);
    mapper.insertEntryKeyValue(entry, "expirationdates", expirationDate);

    List<StockCard> stockCards = mapper.queryStockCardBasicInfo(defaultFacility.getId());
    assertThat(stockCards.size(), is(1));
    assertThat(stockCards.get(0).getProduct().getCode(), is(ProductBuilder.PRODUCT_CODE));
  }

  @Test
  public void shouldReturnStockCardEntryByOccurredDateRange() throws Exception {

    StockCardEntry entry = getStockCardEntry();
    entry.setOccurred(DateUtil.parseDate("2015-11-12 00:00:00"));
    mapper.insertEntry(entry);

    StockCardEntry entry2 = getStockCardEntry();
    entry2.setOccurred(DateUtil.parseDate("2015-11-13 00:00:00"));
    mapper.insertEntry(entry2);

    StockCardEntry entry3 = getStockCardEntry();
    entry3.setOccurred(DateUtil.parseDate("2015-11-14 00:00:00"));
    mapper.insertEntry(entry3);

    Date startDate = DateUtil.parseDate("2015-11-12 00:00:00");
    Date endDate = DateUtil.parseDate("2015-11-13 00:00:00");

    List<StockCardEntry> stockCardsEntries = mapper.queryStockCardEntriesByDateRange(defaultCard.getId(),
        startDate, endDate);
    assertThat(stockCardsEntries.size(), is(1));
  }


  @Test
  public void shouldReturnStockCardLatestExpirationDates() throws Exception {
    StockCardEntry entry = getStockCardEntry();
    String expirationDate = "2015/1/1";
    mapper.insertEntry(entry);
    mapper.insertEntryKeyValue(entry, "expirationdates", expirationDate);

    String latestExpirationDates = mapper.getStockCardLatestExpirationDates(defaultCard.getId());
    assertThat(latestExpirationDates, is(expirationDate));
  }

  private void updateModifiedDateForStockCard(Timestamp modifiedDate, Long stockCardId) throws SQLException {
    queryExecutor.executeUpdate("UPDATE stock_cards SET modifieddate = ? WHERE id = ?", modifiedDate, stockCardId);
  }

  @Test
  public void shouldUpdateAllStockCardsWithFacilityId() throws InterruptedException {
    insertTwoMoreStockCardsForDefaultFacility();

    int numOfResults = mapper.updateAllStockCardSyncTimeForFacilityToNow(defaultFacility.getId());

    assertEquals(3, numOfResults);
  }

  @Test
  public void shouldUpdateStockCardsNotInProductCodeList() throws Exception {
    insertTwoMoreStockCardsForDefaultFacility();

    int numOfResults = mapper.updateStockCardToSyncTimeToNow(defaultFacility.getId(), "code2");

    assertEquals(1, numOfResults);
  }

  private void insertTwoMoreStockCardsForDefaultFacility() {
    Product product1 = make(a(ProductBuilder.defaultProduct, with(active, true), with(code, "Prod1")));
    Product product2 = make(a(ProductBuilder.defaultProduct, with(active, true), with(code, "code2")));
    productMapper.insert(product1);
    productMapper.insert(product2);

    stockCard1 = new StockCard();
    stockCard1.setFacility(defaultFacility);
    stockCard1.setProduct(product1);
    stockCard2 = new StockCard();
    stockCard2.setFacility(defaultFacility);
    stockCard2.setProduct(product2);
    mapper.insert(stockCard1);
    mapper.insert(stockCard2);
  }
}
