package org.openlmis.stockmanagement.domain;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class LotMovementItem extends BaseModel {

    private StockCardEntry stockCardEntry;

    private Lot lot;

    private Long quantity;

    private List<LotMovementItemKV> extensions = new ArrayList<>();

    public LotMovementItem(Lot lot, Long quantity, StockCardEntry entry) {
        this.lot = lot;
        this.quantity = quantity;
    }

    public void addKeyValue(String key, String value) {
        String newKey = key.trim().toLowerCase();
        extensions.add(new LotMovementItemKV(newKey, value, new Date()));
    }
}
