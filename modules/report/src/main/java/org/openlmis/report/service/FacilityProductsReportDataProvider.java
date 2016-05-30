package org.openlmis.report.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.report.model.dto.FacilityProductReportEntry;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.repository.CMMRepository;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
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
    private static final String DDM_CODE = "DDM";
    private static final String DPM_CODE = "DPM";

    @Autowired
    private GeographicZoneMapper geographicZoneMapper;

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private StockCardMapper stockCardMapper;

    @Autowired
    private CMMRepository cmmRepository;

    public List<FacilityProductReportEntry> getReportDataForSingleProduct(final Long geographicZoneId, final Long productId, final Date endTime) {
        List<Facility> facilities = getAllHealthFacilities();
        final GeographicZone geographicZone = geographicZoneMapper.getWithParentById(geographicZoneId);

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

    public List<FacilityProductReportEntry> getReportDataForAllProducts(Long facilityId, final Date endTime) {
        List<StockCard> stockCards = stockCardMapper.getAllByFacility(facilityId);
        final Facility facility = facilityMapper.getById(facilityId);
        return from(stockCards).transform(getReportEntry(endTime, facility)).toList();
    }

    protected static boolean inGeographicZone(GeographicZone geographicZone, Facility facility) {
        if (DISTRICT_CODE.equalsIgnoreCase(geographicZone.getLevel().getCode())) {
            return geographicZone.getCode().equals(facility.getGeographicZone().getCode());
        } else if (PROVINCE_CODE.equalsIgnoreCase(geographicZone.getLevel().getCode())) {
            return geographicZone.getCode().equals(facility.getGeographicZone().getParent().getCode());
        }
        return false;
    }

    @Transactional
    protected List<Facility> getAllHealthFacilities() {
        List<Facility> allReportFacilities = facilityMapper.getAllReportFacilities();

        return from(allReportFacilities).filter(new Predicate<Facility>() {
            @Override
            public boolean apply(Facility input) {
                return isHealthFacility(input);
            }
        }).toList();
    }

    private List<FacilityProductReportEntry> fillReportEntryList(final Long productId, final Date endTime, List<Facility> facilities) {
        List<FacilityProductReportEntry> reportEntryList = new ArrayList<>();
        for (Facility facility : facilities) {
            List<StockCard> stockCards = stockCardMapper.getAllByFacility(facility.getId());

            Optional<FacilityProductReportEntry> entryOptional = from(stockCards).firstMatch(new Predicate<StockCard>() {
                @Override
                public boolean apply(StockCard stockCard) {
                    return stockCard.getProduct().getId().equals(productId);
                }
            }).transform(getReportEntry(endTime, facility));

            if (entryOptional.isPresent()) {
                reportEntryList.add(entryOptional.get());
            }
        }
        return reportEntryList;
    }

    private Function<StockCard, FacilityProductReportEntry> getReportEntry(final Date endTime, final Facility facility) {
        return new Function<StockCard, FacilityProductReportEntry>() {
            @Override
            public FacilityProductReportEntry apply(StockCard stockCard) {
                FacilityProductReportEntry entry = new FacilityProductReportEntry(stockCard, endTime);
                entry.setFacilityCode(facility.getCode());
                entry.setFacilityName(facility.getName());
                entry.setCmm(cmmRepository.getCmmValue(facility.getId(), stockCard.getProduct().getCode(), endTime));
                return entry;
            }
        };
    }

    private boolean isHealthFacility(Facility input) {
        return !input.getFacilityType().getCode().equalsIgnoreCase(DDM_CODE) && !input.getFacilityType().getCode().equalsIgnoreCase(DPM_CODE);
    }
}
