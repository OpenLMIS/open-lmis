package org.openlmis.report.model.dto;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


public class FacilityProductReportEntryTest {


  private StockCard stockCard;
  private FacilityProductReportEntry firstReportEntry;
  private FacilityProductReportEntry secondReportEntry;

  @Before
  public void setUp() throws Exception {
    setupStockCardWithTwoEntries();
    firstReportEntry = new FacilityProductReportEntry(stockCard, DateUtil.parseDate("2018-10-11 10:10:10"));
    secondReportEntry = new FacilityProductReportEntry(stockCard, DateUtil.parseDate("2018-10-12 10:10:10"));
  }

  @Test
  public void shouldGetCorrectProductQuantity() throws Exception {
    assertThat(firstReportEntry.getProductQuantity(),is(100L));
    assertThat(secondReportEntry.getProductQuantity(),is(200L));
  }

  @Test
  public void shouldGetCorrectSoonestExpiryDate() throws Exception {
    assertThat(DateUtil.getFormattedDate(firstReportEntry.getSoonestExpiryDate(),DateUtil.FORMAT_DATE_TIME_DAY_MONTH_YEAR),is("10/10/2019"));
    assertThat(DateUtil.getFormattedDate(secondReportEntry.getSoonestExpiryDate(),DateUtil.FORMAT_DATE_TIME_DAY_MONTH_YEAR),is("10/10/2017"));
  }

  @Test
  public void shouldGetSoonestExpiryDateIsNULLIfIsBeforeSearchDate() throws Exception {
    FacilityProductReportEntry reportEntryBeforeSearchDate = new FacilityProductReportEntry(stockCard, DateUtil.parseDate("2018-10-09 10:10:10"));
    assertNull(DateUtil.getFormattedDate(reportEntryBeforeSearchDate.getSoonestExpiryDate(),DateUtil.FORMAT_DATE_TIME_DAY_MONTH_YEAR));
  }

  public void setupStockCardWithTwoEntries() {
    Facility facility;
    Product product;
    facility = make(a(FacilityBuilder.defaultFacility));
    product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.productId, 1L)));

    stockCard = new StockCard();
    stockCard.setProduct(product);
    stockCard.setFacility(facility);

    StockCardEntry stockCardEntry = new StockCardEntry();
    stockCardEntry.setQuantity(100L);
    stockCardEntry.setCreatedDate(DateUtil.parseDate("2018-10-10 10:10:10"));

    ArrayList<StockCardEntryKV> keyValues = new ArrayList<>();
    keyValues.add(new StockCardEntryKV(FacilityProductReportEntry.EXPIRATION_DATES, "10/10/2019, 10/21/2020", null));
    stockCardEntry.setKeyValues(keyValues);

    StockCardEntry secondStockCardEntry = new StockCardEntry();
    secondStockCardEntry.setQuantity(100L);
    secondStockCardEntry.setCreatedDate(DateUtil.parseDate("2018-10-12 10:10:10"));

    ArrayList<StockCardEntryKV> secondKeyValues = new ArrayList<>();
    secondKeyValues.add(new StockCardEntryKV(FacilityProductReportEntry.EXPIRATION_DATES, "10/10/2017, 10/20/2020", null));
    secondStockCardEntry.setKeyValues(secondKeyValues);

    List<StockCardEntry> entries = asList(secondStockCardEntry,stockCardEntry);
    stockCard.setEntries(entries);
  }

}