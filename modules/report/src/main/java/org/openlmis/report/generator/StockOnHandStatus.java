package org.openlmis.report.generator;

import org.apache.poi.ss.usermodel.IndexedColors;

public enum StockOnHandStatus {

    REGULAR_STOCK(IndexedColors.GREEN.getIndex(), "stock.cmm.regular.stock", "regularStock"),

    LOW_STOCK(IndexedColors.YELLOW.getIndex(), "stock.cmm.low.stock", "lowStock"),

    STOCK_OUT(IndexedColors.RED.getIndex(), "stock.cmm.stock.out", "stockOut"),

    OVER_STOCK(IndexedColors.VIOLET.getIndex(), "stock.cmm.over.stock", "overStock");

    private short colorIndex;

    private String messageKey;

    private String description;

    StockOnHandStatus(short index, String key, String desc) {
        colorIndex = index;
        messageKey = key;
        description = desc;
    }

    public short getColorIndex() {
        return colorIndex;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getDescription() {
        return description;
    }
}
