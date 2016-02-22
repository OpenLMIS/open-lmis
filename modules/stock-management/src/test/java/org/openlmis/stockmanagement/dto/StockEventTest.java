package org.openlmis.stockmanagement.dto;

import org.joda.time.DateTime;
import org.junit.Test;
import org.openlmis.stockmanagement.builder.StockEventBuilder;

import java.util.HashMap;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StockEventTest {
    @Test
    public void shouldGenerateConsistentSyncUpHash() throws Exception {
        StockEvent stockEvent = getStockEvent("2015-01-02");

        String syncUpHash = stockEvent.getSyncUpHash();

        assertThat(syncUpHash, is("LBERNcuipWDZUmEUg6dyhwLeTSQqUipAZxjCsJOVeeJ03vPYO0isjii7Iqzz4AsIHvzipQbkb5LzlWkOLlzxOXv92RQieie"));
    }

    @Test
    public void shouldGenerateSameHashForDifferentEventObjectsThatHaveSameContent() throws Exception {
        StockEvent stockEvent1 = getStockEvent("2015-01-03");
        StockEvent stockEvent2 = getStockEvent("2015-01-03");

        String syncUpHash1 = stockEvent1.getSyncUpHash();
        String syncUpHash2 = stockEvent2.getSyncUpHash();

        assertEquals(syncUpHash1, syncUpHash2);
    }

    @Test
    public void shouldGenerateDifferentHashForEventsThatHaveDifferentContent() throws Exception {
        StockEvent stockEvent1 = getStockEvent("2015-01-03");
        StockEvent stockEvent2 = getStockEvent("2015-01-04");

        String syncUpHash1 = stockEvent1.getSyncUpHash();
        String syncUpHash2 = stockEvent2.getSyncUpHash();

        assertNotSame(syncUpHash1, syncUpHash2);
    }

    private StockEvent getStockEvent(String createdDate) {
        StockEvent stockEvent = make(a(StockEventBuilder.defaultStockEvent,
                with(StockEventBuilder.productCode, "code1"),
                with(StockEventBuilder.reasonName, "reason1"),
                with(StockEventBuilder.quantity, 1L),
                with(StockEventBuilder.type, StockEventType.ISSUE)));

        HashMap<String, String> props = new HashMap<>();
        props.put("SOH", "123");
        stockEvent.setCustomProps(props);
        stockEvent.setOccurred(DateTime.parse("2015-01-01").toDate());
        stockEvent.setCreatedTime(DateTime.parse(createdDate).toDate());
        return stockEvent;
    }
}
