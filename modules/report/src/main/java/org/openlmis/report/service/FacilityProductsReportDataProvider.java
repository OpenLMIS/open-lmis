package org.openlmis.report.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.model.dto.FacilityProductReportEntry;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.service.StockCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class FacilityProductsReportDataProvider {

    @Autowired
    private FacilityService facilityService;
    @Autowired
    private StockCardService stockCardService;

    public List<FacilityProductReportEntry> getReportData(Long geographicZoneId, final Long productId, final Date endTime) {
        List<FacilityProductReportEntry> reportEntryList = new ArrayList<>();
        List<Facility> facilities = facilityService.getAllForGeographicZone(geographicZoneId);
        for (Facility facility : facilities) {
            List<StockCard> stockCards = stockCardService.getStockCards(facility.getId());

            Optional<FacilityProductReportEntry> entryOptional = FluentIterable.from(stockCards).firstMatch(new Predicate<StockCard>() {
                @Override
                public boolean apply(StockCard input) {
                    return input.getProduct().getId().equals(productId);
                }
            }).transform(new Function<StockCard, FacilityProductReportEntry>() {
                @Override
                public FacilityProductReportEntry apply(StockCard input) {
                    return new FacilityProductReportEntry(input, endTime);
                }
            });

            if (entryOptional.isPresent()) {
                FacilityProductReportEntry entry = entryOptional.get();
                entry.setFacilityName(facility.getName());
                reportEntryList.add(entry);
            }
        }
        return reportEntryList;
    }
}
