package org.openlmis.stockmanagement.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.SetUniqueList;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockManagementUtils {
    public static Map<String, String> getKeyValueAggregate(List<StockCardEntryKV> keyValues,
                                                           StockCardEntryKVReduceStrategy strategy) {
        Map<String, String> returnMap = new HashMap<>();

        // Get just the keys in the key-value list
        Collection keys = CollectionUtils.collect(keyValues, new Transformer() {
            @Override
            public Object transform(Object o) {
                return ((StockCardEntryKV) o).getKeyColumn();
            }
        });

        // Get only the unique keys
        SetUniqueList.decorate((List) keys);

        // Iterate through the keys, getting the sub-list matching the key. Then implement the strategy on the sub-list.
        // Put the resulting key-value entry into the map.
        for (final Object item : keys) {
            List<StockCardEntryKV> subList = (List<StockCardEntryKV>)CollectionUtils.select(keyValues, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    return ((StockCardEntryKV)o).getKeyColumn().equalsIgnoreCase((String)item);
                }
            });
            StockCardEntryKV entry = strategy.reduce(subList);
            returnMap.put(entry.getKeyColumn(), entry.getValueColumn());
        }

        return returnMap;
    }
}
