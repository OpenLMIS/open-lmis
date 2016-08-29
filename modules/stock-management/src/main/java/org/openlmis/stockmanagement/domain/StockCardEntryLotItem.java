package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class StockCardEntryLotItem extends BaseModel {

    @JsonIgnore
    private Long stockCardEntryId;

    private Lot lot;

    private Long quantity;

    private Date effectiveDate;

    private List<StockCardEntryLotItemKV> extensions = new ArrayList<>();

    public StockCardEntryLotItem(Lot lot, Long quantity) {
        this.lot = lot;
        this.quantity = quantity;
    }

    public void addKeyValue(String key, String value) {
        String newKey = key.trim().toLowerCase();
        extensions.add(new StockCardEntryLotItemKV(newKey, value, new Date()));
    }
}
