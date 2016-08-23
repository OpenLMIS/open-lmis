package org.openlmis.stockmanagement.domain;

import java.util.List;

public class LotMovementItem {

    private StockCardEntry stockCardEntry;

    private Lot lot;

    private Long quantity;

    private List<LotMovementItemKV> extensions;
}
