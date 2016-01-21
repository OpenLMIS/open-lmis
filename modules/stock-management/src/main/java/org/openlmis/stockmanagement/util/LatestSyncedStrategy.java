package org.openlmis.stockmanagement.util;

import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.util.Date;
import java.util.List;

public class LatestSyncedStrategy implements StockCardEntryKVReduceStrategy {

    @Override
    public StockCardEntryKV reduce(List<StockCardEntryKV> list) {
        if (null == list || list.isEmpty()) return null;
        StockCardEntryKV max = new StockCardEntryKV("","",new Date(0));
        for (StockCardEntryKV item : list) {
            if (item.getSyncedDate().after(max.getSyncedDate())) {
                max = item;
            }
        }
        return max;
    }
}
