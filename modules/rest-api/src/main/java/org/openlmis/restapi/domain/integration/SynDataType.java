package org.openlmis.restapi.domain.integration;

import org.openlmis.core.exception.DataException;

public enum SynDataType {

    SOH("soh", "stock_on_hand_product_vw", 1000),
    MOVEMENT("movement", "vw_stock_movements_integration", 1000),
    REQUISITION("requisition", "requisitions", 100);

    private String name;

    private String tableName;

    private int count;

    SynDataType(String name, String tableName, int count) {
        this.name = name;
        this.tableName = tableName;
        this.count = count;
    }

    public String getTableName() {
        return tableName;
    }

    public int getCount() {
        return count;
    }

    public static SynDataType getSynDataType(String name) {
        SynDataType[] synDataTypes = SynDataType.values();
        for (SynDataType synDataType : synDataTypes) {
            if (synDataType.name.equals(name)) {
                return synDataType;
            }
        }
        throw new DataException("Please use right type!");
    }
}
