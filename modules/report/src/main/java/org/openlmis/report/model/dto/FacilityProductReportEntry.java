package org.openlmis.report.model.dto;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

@Data
@NoArgsConstructor
public class FacilityProductReportEntry {
    String productName;
    String facilityName;
    long productQuantity;
    Date soonestExpiryDate;
    Date lastSyncDate;
    String code;

    public FacilityProductReportEntry(StockCard stockCard, Date endTime) {
        if (endTime == null) {
            endTime = new Date();
        }

        this.productName = stockCard.getProduct().getName();
        List<StockCardEntry> stockCardEntryList = filterEntryByDate(stockCard, endTime);
        this.productQuantity = calculateQuantity(stockCardEntryList);

        getSoonestExpirationDate(from(stockCardEntryList).last().get());

        this.lastSyncDate = stockCard.getModifiedDate();
        this.code = stockCard.getProduct().getCode();
    }

    protected void getSoonestExpirationDate(StockCardEntry lastEntry) {
        if (lastEntry == null) {
            return;
        }

        String expirationDates = lastEntry.getCustomProps().get("expirationdates");
        if (!StringUtils.isEmpty(expirationDates)) {
            String[] dateStrings = expirationDates.split(",");

            Optional<Date> soonestDate = from(sortExpirationDate(dateStrings)).last();
            if (soonestDate.isPresent()) {
                this.soonestExpiryDate = soonestDate.get();
            }
        }
    }

    private ImmutableList<Date> sortExpirationDate(String[] dateStrings) {
        return from(newArrayList(dateStrings)).transform(new Function<String, Date>() {
                    @Override
                    public Date apply(String input) {
                        return DateUtil.parseDate(input, "dd/MM/yyyy");
                    }
                }).toSortedList(new Comparator<Date>() {
                    @Override
                    public int compare(Date o1, Date o2) {
                        return o1.compareTo(o2);
                    }
                });
    }

    private List<StockCardEntry> filterEntryByDate(final StockCard stockCard, final Date date) {
        return from(stockCard.getEntries()).filter(new Predicate<StockCardEntry>() {
            @Override
            public boolean apply(StockCardEntry input) {
                return !DateUtils.truncate(input.getCreatedDate(), Calendar.DATE).after(date);
            }
        }).toList();
    }

    private long calculateQuantity(List<StockCardEntry> stockCardEntryList) {
        long result = 0;
        for (StockCardEntry entry : stockCardEntryList) {
            result += entry.getQuantity();
        }
        return result;
    }
}
