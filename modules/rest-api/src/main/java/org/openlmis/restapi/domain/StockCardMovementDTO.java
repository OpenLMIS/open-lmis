package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.domain.StockCardEntryType;

import java.util.Date;
import java.util.HashMap;

import static java.lang.Math.abs;

@Data
@NoArgsConstructor
public class StockCardMovementDTO {

    String documentNumber;

    long movementQuantity;

    String reason;

    StockCardEntryType type;

    HashMap<String,String> extensions = new HashMap<>();

    Date createdDate;

    Date movementDate;

    public StockCardMovementDTO(StockCardEntry stockCardEntry) {
        this.documentNumber = stockCardEntry.getReferenceNumber();
        this.movementQuantity = abs(stockCardEntry.getQuantity());
        this.reason = stockCardEntry.getAdjustmentReason().getName();
        this.initCustomProps(stockCardEntry);
        this.movementDate = stockCardEntry.getOccurred();
        this.createdDate = stockCardEntry.getCreatedDate();
        this.type = stockCardEntry.getType();
    }

    private void initCustomProps(StockCardEntry stockCardEntry) {
        for (StockCardEntryKV stockCardEntryKV :stockCardEntry.getExtensions()){
            this.extensions.put(stockCardEntryKV.getKey(), stockCardEntryKV.getValue());
        }
    }
}
