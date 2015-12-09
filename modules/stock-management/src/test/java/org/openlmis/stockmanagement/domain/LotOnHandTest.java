package org.openlmis.stockmanagement.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTests.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class LotOnHandTest {

    @Test
    public void shouldGetNullCustomPropsFromEmptyKeyValues() {
        StockCardEntry entry = new StockCardEntry(new StockCard(), StockCardEntryType.ADJUSTMENT, 1L, null, null);

        Map<String, String> customProps = entry.getCustomProps();

        assertNull(customProps);
    }

    @Test
    public void shouldGetCustomPropsFromKeyValues() {
        List<StockCardEntryKV> keyValues = new ArrayList<>();
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue1", new Date()));
        keyValues.add(new StockCardEntryKV("testkey2", "testvalue2", new Date()));
        LotOnHand lotOnHand = LotOnHand.createZeroedLotOnHand(new Lot(), new StockCard());
        lotOnHand.setKeyValues(keyValues);

        Map<String, String> customProps = lotOnHand.getCustomProps();

        assertEquals(customProps.size(), 2);
        assertEquals(customProps.get("testkey1"), "testvalue1");
        assertEquals(customProps.get("testkey2"), "testvalue2");
    }

    @Test
    public void shouldGetReducedCustomPropsFromDuplicateKeys() {
        List<StockCardEntryKV> keyValues = new ArrayList<>();
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue1", new Date(2)));
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue2", new Date(3)));
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue3", new Date(1)));
        LotOnHand lotOnHand = LotOnHand.createZeroedLotOnHand(new Lot(), new StockCard());
        lotOnHand.setKeyValues(keyValues);

        Map<String, String> customProps = lotOnHand.getCustomProps();

        assertEquals(customProps.size(), 1);
        assertEquals(customProps.get("testkey1"), "testvalue2");
    }

    @Test
    public void shouldGetReducedCustomPropsFromMultipleDuplicateKeys() {
        List<StockCardEntryKV> keyValues = new ArrayList<>();
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue1", new Date(2)));
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue2", new Date(3)));
        keyValues.add(new StockCardEntryKV("testkey1", "testvalue3", new Date(1)));
        keyValues.add(new StockCardEntryKV("testkey2", "testvalue2", new Date(2)));
        keyValues.add(new StockCardEntryKV("testkey2", "testvalue3", new Date(3)));
        keyValues.add(new StockCardEntryKV("testkey2", "testvalue1", new Date(1)));
        LotOnHand lotOnHand = LotOnHand.createZeroedLotOnHand(new Lot(), new StockCard());
        lotOnHand.setKeyValues(keyValues);

        Map<String, String> customProps = lotOnHand.getCustomProps();

        assertEquals(customProps.size(), 2);
        assertEquals(customProps.get("testkey1"), "testvalue2");
        assertEquals(customProps.get("testkey2"), "testvalue3");
    }
}
