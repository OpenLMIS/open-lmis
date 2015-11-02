package org.openlmis.report.model.dto;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class FacilityProductReportEntry {
    String productName;
    String facilityName;
    long productQuantity;
    Date soonestExpiryDate;
    Date lastSyncDate;

    public FacilityProductReportEntry(StockCard stockCard, Date endTime){
        if (endTime == null){
            endTime = new Date();
        }

        this.productName = stockCard.getProduct().getName();
        this.productQuantity = calculateQuantity(filterEntryByDate(stockCard, endTime));

        // TODO need change
        this.soonestExpiryDate = new Date();
        this.lastSyncDate = stockCard.getModifiedDate();

    }

    private List<StockCardEntry> filterEntryByDate(final StockCard stockCard, final Date date){
        return FluentIterable.from(stockCard.getEntries()).filter(new Predicate<StockCardEntry>() {
            @Override
            public boolean apply(StockCardEntry input) {
                return !DateUtils.truncate(input.getCreatedDate(), Calendar.DATE).after(date);
            }
        }).toList();
    }

    private long calculateQuantity(List<StockCardEntry> stockCardEntryList){
        long result = 0;
        for (StockCardEntry entry : stockCardEntryList){
            result += entry.getQuantity();
        }
        return result;
    }
}
