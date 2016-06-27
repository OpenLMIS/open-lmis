package org.openlmis.web.controller.cubesreports.validation;

import com.google.common.base.Predicate;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@Service
public class CubesReportValidationService {

    @Autowired
    private ProfileBaseLookupService profileBaseLookupService;

    public boolean isQueryValid(String queryUri, String queryString) {
        String facilityTypeCode = profileBaseLookupService.getCurrentUserFacility().getFacilityType().getCode();
        final CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(MozFacilityTypes.valueOf(facilityTypeCode), queryString);
        if (cubesAccessInfo.isLocationInfoMissing()) {
            return false;
        }

        List<GeographicZone> legalZones = profileBaseLookupService.getAllZones();
        if (cubesAccessInfo.getCurrentUserFacilityType() == DNM) {
            return true;
        } else if (cubesAccessInfo.getCurrentUserFacilityType() == DPM) {
            return from(legalZones).anyMatch(isProvinceMatch(cubesAccessInfo));
        } else if (cubesAccessInfo.getCurrentUserFacilityType() == DDM) {
            return from(legalZones).anyMatch(isDistrictMatch(cubesAccessInfo));
        }

        return false;
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
}
