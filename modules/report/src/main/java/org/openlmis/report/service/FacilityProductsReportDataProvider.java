package org.openlmis.report.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.report.model.dto.FacilityProductReportEntry;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.service.StockCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

@Service
@NoArgsConstructor
public class FacilityProductsReportDataProvider {

    public static final String DISTRICT_CODE = "district";
    public static final String PROVINCE_CODE = "province";
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private StockCardService stockCardService;
    @Autowired
    private GeographicZoneService geographicZoneService;

    @Autowired
    private ReportLookupService reportLookupService;

    @Autowired
    private FacilityMapper facilityMapper;

    public List<FacilityProductReportEntry> getReportData(final Long geographicZoneId, final Long productId, final Date endTime) {
        List<Facility> facilities = getAllFacilities();
        final GeographicZone geographicZone = geographicZoneService.getById(geographicZoneId);

        if (geographicZone != null) {

            facilities = from(facilities).filter(new Predicate<Facility>() {
                @Override
                public boolean apply(Facility facility) {
                    return inGeographicZone(geographicZone, facility);
                }
            }).toList();
        }

        return fillReportEntryList(productId, endTime, facilities);
    }

    public List<FacilityProductReportEntry> getReportData(Long facilityId, final Date endTime) {
        List<StockCard> stockCards = stockCardService.getStockCards(facilityId);
        final Facility facility = facilityService.getById(facilityId);
        return from(stockCards).transform(getReportEntry(endTime)).transform(new Function<FacilityProductReportEntry, FacilityProductReportEntry>() {
            @Override
            public FacilityProductReportEntry apply(FacilityProductReportEntry input) {
                input.setFacilityName(facility.getName());
                return input;
            }
        }).toList();
    }

    protected static boolean inGeographicZone(GeographicZone geographicZone, Facility facility) {
        if (DISTRICT_CODE.equalsIgnoreCase(geographicZone.getLevel().getCode())) {
            return geographicZone.getCode().equals(facility.getGeographicZone().getCode());
        } else if (PROVINCE_CODE.equalsIgnoreCase(geographicZone.getLevel().getCode())) {
            return geographicZone.getCode().equals(facility.getGeographicZone().getParent().getCode());
        }
        return false;
    }

    protected List<FacilityProductReportEntry> fillReportEntryList(final Long productId, final Date endTime, List<Facility> facilities) {
        List<FacilityProductReportEntry> reportEntryList = new ArrayList<>();
        for (Facility facility : facilities) {
            List<StockCard> stockCards = stockCardService.getStockCards(facility.getId());

            Optional<FacilityProductReportEntry> entryOptional = from(stockCards).firstMatch(new Predicate<StockCard>() {
                @Override
                public boolean apply(StockCard input) {
                    return input.getProduct().getId().equals(productId);
                }
            }).transform(getReportEntry(endTime));

            if (entryOptional.isPresent()) {
                FacilityProductReportEntry entry = entryOptional.get();
                entry.setFacilityName(facility.getName());
                reportEntryList.add(entry);
            }
        }
        return reportEntryList;
    }

    private Function<StockCard, FacilityProductReportEntry> getReportEntry(final Date endTime) {
        return new Function<StockCard, FacilityProductReportEntry>() {
            @Override
            public FacilityProductReportEntry apply(StockCard input) {
                return new FacilityProductReportEntry(input, endTime);
            }
        };
    }

    @Transactional
    protected List<Facility> getAllFacilities() {
        return facilityMapper.getAllReportFacilities();
    }
}
