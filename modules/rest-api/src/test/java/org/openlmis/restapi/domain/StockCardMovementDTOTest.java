package org.openlmis.restapi.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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

  private StockCardEntry getStockCardEntry() {
    StockCardEntry entry = new StockCardEntry();
    entry.setAdjustmentReason(StockAdjustmentReason.create("some reason"));
    entry.setQuantity(100L);
    entry.setExtensions(new ArrayList<StockCardEntryKV>());
    return entry;
  }

}