package org.openlmis.stockmanagement.util;

import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.util.List;

public interface StockCardEntryKVReduceStrategy {
    StockCardEntryKV reduce(List<StockCardEntryKV> list);
}
