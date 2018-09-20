package org.openlmis.web.controller.cubesreports.validation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.model.dto.Facility;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.apache.ibatis.session.RowBounds.NO_ROW_LIMIT;
import static org.apache.ibatis.session.RowBounds.NO_ROW_OFFSET;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@Service
public class CubesReportValidationService {
    private static List<String> excludedCubes = asList("products");

    @Autowired
    private ProfileBaseLookupService profileBaseLookupService;

    @Autowired
    private GeographicZoneRepository geographicZoneRepository;

    public CubesAccessInfo validate(final String queryUri, final String queryString) {
        final CubesAccessInfo cubesAccessInfo = createAccessInfo(queryString);
        boolean isCubeExcluded = isCubeExcluded(queryUri);
        if (isCubeExcluded) {
            cubesAccessInfo.setValid(true);
            return cubesAccessInfo;
        } else {
            boolean isLocationAccessAllowed = isLocationAccessAllowed(cubesAccessInfo);
            cubesAccessInfo.setValid(isLocationAccessAllowed);
            return cubesAccessInfo;
        }
    }

    private boolean isLocationAccessAllowed(CubesAccessInfo cubesAccessInfo) {
        List<GeographicZone> legalZones = profileBaseLookupService.getAllZones();
        List<Facility> legalFacilities = profileBaseLookupService.getAllFacilities(new RowBounds(NO_ROW_OFFSET, NO_ROW_LIMIT));

        fillMissingLocations(cubesAccessInfo, legalZones, legalFacilities);

        if (cubesAccessInfo.getCurrentUserFacilityType() == Central) {
            return true;
        } else if (cubesAccessInfo.getCurrentUserFacilityType() == DPM) {
            return from(legalZones).anyMatch(isProvinceMatch(cubesAccessInfo));
        } else if (cubesAccessInfo.getCurrentUserFacilityType() == DDM) {
            return from(legalZones).anyMatch(isDistrictMatch(cubesAccessInfo));
        } else {
            return from(legalFacilities).anyMatch(isFacilityMatch(cubesAccessInfo));
        }
    }

    private CubesAccessInfo createAccessInfo(String queryString) {
        String facilityTypeCode = profileBaseLookupService.getCurrentUserFacility().getFacilityType().getCode();
        return CubesAccessInfo.createInstance(MozFacilityTypes.getEnum(facilityTypeCode), queryString);
    }

    private void fillMissingLocations(CubesAccessInfo cubesAccessInfo, List<GeographicZone> legalZones, List<Facility> legalFacilities) {
        cubesAccessInfo.fillMissingLocation(from(legalZones).transform(new Function<GeographicZone, org.openlmis.core.domain.GeographicZone>() {
            @Override
            public org.openlmis.core.domain.GeographicZone apply(GeographicZone zone) {
                return geographicZoneRepository.getByCode(zone.getCode());
            }
        }).toList(), legalFacilities);
    }

    private Predicate<Facility> isFacilityMatch(final CubesAccessInfo cubesAccessInfo) {
        return new Predicate<Facility>() {
            @Override
            public boolean apply(Facility facility) {
                return facility.getCode().equals(cubesAccessInfo.getFacility());
            }
        };
    }

    private Predicate<GeographicZone> isDistrictMatch(final CubesAccessInfo cubesAccessInfo) {
        return new Predicate<GeographicZone>() {
            @Override
            public boolean apply(GeographicZone zone) {
                return zone.getCode().equals(cubesAccessInfo.getDistrict());
            }
        };
    }

    private Predicate<GeographicZone> isProvinceMatch(final CubesAccessInfo cubesAccessInfo) {
        return new Predicate<GeographicZone>() {
            @Override
            public boolean apply(GeographicZone zone) {
                return zone.getCode().equals(cubesAccessInfo.getProvince());
            }
        };
    }

    private boolean isCubeExcluded(final String queryUri) {
        return from(excludedCubes).anyMatch(new Predicate<String>() {
            @Override
            public boolean apply(String excludedCube) {
                return queryUri.contains(excludedCube);
            }
        });
    }
}
