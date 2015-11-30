package org.openlmis.report.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.*;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProductsReportDataProviderTest {

  @Mock
  FacilityMapper facilityMapper;

  @Mock
  StockCardMapper stockCardMapper;

  @Mock
  GeographicZoneMapper geographicZoneMapper;

  @InjectMocks
  FacilityProductsReportDataProvider facilityProductsReportDataProvider;

  private List<StockCard> stockCards = new ArrayList<>();
  private List<StockCard> secondFacilityStockCards = new ArrayList<>();
  private Facility facility;
  private Facility secondFacility;
  private Product product;
  private Product secondProduct;
  private StockCard secondStockCard;

  @Test
  public void shouldGetSpecificProductDataForAllFacilitiesInGeographicZone() {
    setupOneFacilityStockCardListWithOneCardOneEntry();
    setupSecondFacilityWithSameFirstProduct();

    when(facilityMapper.getAllReportFacilities()).thenReturn(asList(facility, secondFacility));
    when(stockCardMapper.getAllByFacility(facility.getId())).thenReturn(stockCards);
    when(stockCardMapper.getAllByFacility(secondFacility.getId())).thenReturn(secondFacilityStockCards);
    when(stockCardMapper.getLastUpdatedTimeforStockDataByFacility(facility.getId())).thenReturn(DateUtil.parseDate("2011-11-11 11:11:11"));
    when(stockCardMapper.getLastUpdatedTimeforStockDataByFacility(secondFacility.getId())).thenReturn(DateUtil.parseDate("2013-10-13 10:13:13"));

    List<FacilityProductReportEntry> entryList = facilityProductsReportDataProvider.getReportDataForSingleProduct(facility.getGeographicZone().getId(), product.getId(), DateUtil.parseDate("2018-11-11 11:11:11"));

    assertThat(entryList.size(), is(2));
    assertThat(entryList.get(0).getProductName(), containsString(product.getPrimaryName()));
    assertThat(DateUtil.formatDate(entryList.get(0).getLastSyncDate()), is("2011-11-11 11:11:11"));
    assertThat(DateUtil.formatDate(entryList.get(1).getLastSyncDate()), is("2013-10-13 10:13:13"));
    assertThat(entryList.get(0).getProductQuantity(), is(100L));
    assertThat(entryList.get(1).getProductQuantity(), is(300L));
  }

  @Test
  public void shouldReturnTrueIfFacilityInGeographicZone() {
    GeographicZone district = new GeographicZone();
    district.setCode("District");
    district.setLevel(new GeographicLevel(1L, FacilityProductsReportDataProvider.DISTRICT_CODE, "District", 5));

    GeographicZone province = new GeographicZone();
    province.setCode("Maputo");
    province.setLevel(new GeographicLevel(2L, FacilityProductsReportDataProvider.PROVINCE_CODE, "Province", 6));

    district.setParent(province);

    Facility facility = new Facility();
    facility.setGeographicZone(district);

    assertThat(FacilityProductsReportDataProvider.inGeographicZone(district, facility), is(true));
    assertThat(FacilityProductsReportDataProvider.inGeographicZone(province, facility), is(true));
  }

  @Test
  public void shouldGetOnlyHealthFacilities() {
    Facility healthFacility = new Facility(1L, "HF", "facility1", null, null, new FacilityType("HF"), false);
    Facility DDMFacility = new Facility(1L, "DDM", "facility2", null, null, new FacilityType("DDM"), false);
    Facility DPMFacility = new Facility(1L, "DPM", "facility3", null, null, new FacilityType("DPM"), false);

    List<Facility> facilities = asList(healthFacility, DDMFacility, DPMFacility);
    when(facilityMapper.getAllReportFacilities()).thenReturn(facilities);

    List<Facility> allHealthFacilities = facilityProductsReportDataProvider.getAllHealthFacilities();
    assertThat(allHealthFacilities.size(), is(1));
    assertThat(allHealthFacilities.get(0).getFacilityType().getCode(), is("HF"));
  }

  @Test
  public void shouldGetStockCardDataForOneFacility() {
    setupOneFacilityStockCardListWithOneCardOneEntry();
    setupSecondStockCardOneEntry();
    stockCards.add(secondStockCard);

    when(stockCardMapper.getAllByFacility(facility.getId())).thenReturn(stockCards);
    when(facilityMapper.getById(facility.getId())).thenReturn(facility);
    when(stockCardMapper.getLastUpdatedTimeforStockDataByFacility(facility.getId())).thenReturn(DateUtil.parseDate("2011-11-11 11:11:11"));

    List<FacilityProductReportEntry> entryList = facilityProductsReportDataProvider.getReportDataForAllProducts(facility.getId(),
        DateUtil.parseDate("2012-12-12 12:12:12"));

    assertThat(entryList.size(), is(2));
    assertThat(entryList.get(0).getProductQuantity(), is(100L));
    assertThat(entryList.get(1).getProductQuantity(), is(200L));
    assertThat(entryList.get(0).getCode(), is(product.getCode()));
    assertThat(entryList.get(1).getCode(), is(secondProduct.getCode()));
    assertThat(DateUtil.formatDate(entryList.get(0).getLastSyncDate()), is("2011-11-11 11:11:11"));
    assertThat(DateUtil.formatDate(entryList.get(1).getLastSyncDate()), is("2011-11-11 11:11:11"));
  }

  private void setupOneFacilityStockCardListWithOneCardOneEntry() {
    facility = make(a(FacilityBuilder.defaultFacility));
    product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.productId, 1L)));

    StockCard stockCard = new StockCard();
    stockCard.setProduct(product);
    stockCard.setFacility(facility);

    StockCardEntry stockCardEntry = new StockCardEntry();
    stockCardEntry.setQuantity(100L);
    stockCardEntry.setCreatedDate(DateUtil.parseDate("2011-10-10 10:10:10"));

    ArrayList<StockCardEntryKV> keyValues = new ArrayList<>();
    keyValues.add(new StockCardEntryKV(FacilityProductReportEntry.EXPIRATION_DATES, null, null));
    stockCardEntry.setKeyValues(keyValues);

    List<StockCardEntry> entries = asList(stockCardEntry);
    stockCard.setEntries(entries);
    stockCards.add(stockCard);
  }

  private StockCard setupSecondStockCardOneEntry() {
    secondStockCard = new StockCard();
    secondProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P111"), with(ProductBuilder.primaryName, "Product Test Name")));
    secondStockCard.setProduct(secondProduct);
    secondStockCard.setFacility(facility);
    StockCardEntry stockCardEntry = new StockCardEntry();
    stockCardEntry.setQuantity(200L);
    stockCardEntry.setCreatedDate(DateUtil.parseDate("2011-10-10 10:10:10"));
    ArrayList<StockCardEntryKV> keyValues = new ArrayList<>();
    keyValues.add(new StockCardEntryKV(FacilityProductReportEntry.EXPIRATION_DATES, null, null));
    stockCardEntry.setKeyValues(keyValues);
    secondStockCard.setEntries(asList(stockCardEntry));
    return secondStockCard;
  }

  private void setupSecondFacilityWithSameFirstProduct() {
    secondFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, 234L)));

    StockCard stockCard = new StockCard();
    stockCard.setProduct(product);
    stockCard.setFacility(secondFacility);

    StockCardEntry stockCardEntry = new StockCardEntry();
    stockCardEntry.setQuantity(300L);
    stockCardEntry.setCreatedDate(DateUtil.parseDate("2011-10-10 10:10:10"));

    List<StockCardEntry> entries = asList(stockCardEntry);
    stockCard.setEntries(entries);
    ArrayList<StockCardEntryKV> keyValues = new ArrayList<>();
    keyValues.add(new StockCardEntryKV(FacilityProductReportEntry.EXPIRATION_DATES, null, null));
    stockCardEntry.setKeyValues(keyValues);

    secondFacilityStockCards.add(stockCard);
  }
}
