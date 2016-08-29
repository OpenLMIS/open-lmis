package org.openlmis.restapi.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.domain.StockCardEntryLotItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class StockCardMovementDTOTest {

  @Test
  public void shouldSetOccurredDateAsAStringWithNoTimeZone() throws ParseException {
    StockCardEntry entry = getStockCardEntry();
    entry.setOccurred(new SimpleDateFormat(DateUtil.FORMAT_DATE).parse("2015-10-10"));
    StockCardMovementDTO stockCardMovementDTO = new StockCardMovementDTO(entry);
    assertThat(stockCardMovementDTO.getOccurred(), is("2015-10-10"));
  }

  @Test
  public void shouldNotSetOccurredIfItDoesNotExist() {
    StockCardEntry entry = getStockCardEntry();
    StockCardMovementDTO stockCardMovementDTO = new StockCardMovementDTO(entry);
    assertNull(stockCardMovementDTO.getOccurred());
  }

  @Test
  public void shouldAddLotIfExists() {
    StockCardEntry entry = getStockCardEntry();
    entry.setStockCardEntryLotItems(asList(getLotItem(entry)));
    StockCardMovementDTO stockCardMovementDTO = new StockCardMovementDTO(entry);
    assertThat(stockCardMovementDTO.getLotMovementItems().get(0).getLotNumber(), is("TEST"));
  }

  private StockCardEntry getStockCardEntry() {
    StockCardEntry entry = new StockCardEntry();
    entry.setAdjustmentReason(StockAdjustmentReason.create("some reason"));
    entry.setQuantity(100L);
    entry.setExtensions(new ArrayList<StockCardEntryKV>());
    return entry;
  }

  private StockCardEntryLotItem getLotItem(StockCardEntry entry) {
    Lot lot = new Lot();
    lot.setLotCode("TEST");
    lot.setExpirationDate(new Date());

    StockCardEntryLotItem stockCardEntryLotItem = new StockCardEntryLotItem(lot, 100L);
    stockCardEntryLotItem.setStockCardEntryId(entry.getId());
    return stockCardEntryLotItem;
  }

}