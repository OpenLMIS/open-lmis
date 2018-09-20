package org.openlmis.report.generator;

import org.apache.poi.ss.usermodel.IndexedColors;

public enum StockOnHandStatus {

    REGULAR_STOCK(IndexedColors.GREEN.getIndex(), "stock.cmm.regular.stock"),

    LOW_STOCK(IndexedColors.YELLOW.getIndex(), "stock.cmm.low.stock"),

    STOCK_OUT(IndexedColors.RED.getIndex(), "stock.cmm.stock.out"),

    OVER_STOCK(IndexedColors.VIOLET.getIndex(), "stock.cmm.over.stock");

    private short colorIndex;

    private String messageKey;

    StockOnHandStatus(short index, String desc) {
        colorIndex = index;
        messageKey = desc;
    }

    public short getColorIndex() {
        return colorIndex;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
