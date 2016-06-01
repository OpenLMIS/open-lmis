package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.domain.StockCardEntryType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static java.lang.Math.abs;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class StockCardMovementDTO {

    String documentNumber;

    long movementQuantity;

    String reason;

    StockCardEntryType type;

    HashMap<String,String> extensions = new HashMap<>();

    Date createdDate;

    String occurred;

    Long requested;

    public StockCardMovementDTO(StockCardEntry stockCardEntry) {
        this.documentNumber = stockCardEntry.getReferenceNumber();
        this.movementQuantity = abs(stockCardEntry.getQuantity());
        this.reason = stockCardEntry.getAdjustmentReason().getName();
        this.initCustomProps(stockCardEntry);
        this.createdDate = stockCardEntry.getCreatedDate();
        if (stockCardEntry.getOccurred() != null) {
            this.occurred = new SimpleDateFormat(DateUtil.FORMAT_DATE).format(stockCardEntry.getOccurred());
        }
        this.type = stockCardEntry.getType();
        this.requested = stockCardEntry.getRequestedQuantity();
    }

    private void initCustomProps(StockCardEntry stockCardEntry) {
        for (StockCardEntryKV stockCardEntryKV :stockCardEntry.getExtensions()){
            this.extensions.put(stockCardEntryKV.getKey(), stockCardEntryKV.getValue());
        }
    }
}
