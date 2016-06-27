package org.openlmis.web.controller.cubesreports.validation;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.core.domain.moz.MozFacilityTypes.DNM;
import static org.openlmis.core.domain.moz.MozFacilityTypes.DPM;

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

        if (cubesAccessInfo.getCurrentUserFacilityType() == DNM) {
            return true;
        } else if (cubesAccessInfo.getCurrentUserFacilityType() == DPM) {
            List<GeographicZone> legalZones = profileBaseLookupService.getAllZones();
            return FluentIterable.from(legalZones).anyMatch(new Predicate<GeographicZone>() {
                @Override
                public boolean apply(GeographicZone zone) {
                    return zone.getCode().equals(cubesAccessInfo.getProvince());
                }
            });
        }

        return false;
    }
}
