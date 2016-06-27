package org.openlmis.web.controller.cubesreports.validation;

import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.openlmis.core.domain.moz.MozFacilityTypes.DNM;

@Service
public class CubesReportValidationService {

    @Autowired
    private ProfileBaseLookupService profileBaseLookupService;

    public boolean isQueryValid(String queryUri, String queryString) {
        String facilityTypeCode = profileBaseLookupService.getCurrentUserFacility().getFacilityType().getCode();
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(MozFacilityTypes.valueOf(facilityTypeCode), queryString);
        if (cubesAccessInfo.isLocationInfoMissing()) {
            return false;
        }

        if (cubesAccessInfo.getCurrentUserFacilityType() == DNM) {
            return true;
        }

        return false;
    }
}
