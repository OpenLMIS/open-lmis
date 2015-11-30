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
import org.openlmis.stockmanagement.domain.StockCardEntryKV;

import java.util.*;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

@Data
@NoArgsConstructor
public class FacilityProductReportEntry {
    private String productName;
    private String facilityName;
    private long productQuantity;
    private Date soonestExpiryDate;
    private Date lastSyncDate;
    private String code;

    public static final String EXPIRATION_DATES = "expirationdates";

    public FacilityProductReportEntry(StockCard stockCard, Date endTime) {
        if (endTime == null) {
            endTime = new Date();
        }

        this.productName = stockCard.getProduct().getPrimaryName();
        List<StockCardEntry> stockCardEntryList = filterEntryByDate(stockCard, endTime);
        this.productQuantity = calculateQuantity(stockCardEntryList);

        assignSoonestExpirationDate(stockCardEntryList);

        this.code = stockCard.getProduct().getCode();
    }

    private String getExpirationDateFromStockCardEntry(StockCardEntry entry){
        Optional<StockCardEntryKV> stockCardEntryKVOptional = from(entry.getKeyValues()).firstMatch(new Predicate<StockCardEntryKV>() {
            @Override
            public boolean apply(StockCardEntryKV input) {
                return EXPIRATION_DATES.equalsIgnoreCase(input.getKeyColumn());
            }
        });

        if (stockCardEntryKVOptional.isPresent()){
            return stockCardEntryKVOptional.get().getValueColumn();
        }
        return StringUtils.EMPTY;
    }

    private void assignSoonestExpirationDate(List<StockCardEntry> stockCardEntryList) {
        if (stockCardEntryList == null || stockCardEntryList.size() == 0) {
            return;
        }

        StockCardEntry lastEntry = from(stockCardEntryList).first().get();

        String expirationDates = getExpirationDateFromStockCardEntry(lastEntry);
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
                        return DateUtil.parseDate(input,DateUtil.FORMAT_DATE_TIME_DAY_MONTH_YEAR );
                    }
                }).toSortedList(new Comparator<Date>() {
                    @Override
                    public int compare(Date o1, Date o2) {
                        return o2.compareTo(o1);
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
