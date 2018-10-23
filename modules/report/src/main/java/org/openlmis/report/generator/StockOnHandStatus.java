package org.openlmis.report.generator;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFColor;

public enum StockOnHandStatus {

    REGULAR_STOCK(IndexedColors.GREEN.getIndex(), "stock.cmm.regular.stock", "regularStock",
            new XSSFColor(new java.awt.Color(140,211,104))),

    LOW_STOCK(IndexedColors.YELLOW.getIndex(), "stock.cmm.low.stock", "lowStock",
            new XSSFColor(new java.awt.Color(250,215,77))),

    STOCK_OUT(IndexedColors.RED.getIndex(), "stock.cmm.stock.out", "stockOut",
            new XSSFColor(new java.awt.Color(245,33,45))),

    OVER_STOCK(IndexedColors.VIOLET.getIndex(), "stock.cmm.over.stock", "overStock",
            new XSSFColor(new java.awt.Color(187,149,227))),

    NOT_EXIST((short)-1, null, null, null);

    private short colorIndex;

    private String messageKey;

    private String description;

    private XSSFColor color;

    StockOnHandStatus(short index, String key, String desc, XSSFColor color) {
        this.colorIndex = index;
        this.messageKey = key;
        this.description = desc;
        this.color = color;
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

    public XSSFColor getColor() {
        return color;
    }
}
