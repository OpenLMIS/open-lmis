package org.openlmis.report.service.lookup;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.report.model.dto.GeographicZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@Service
@NoArgsConstructor
public class ProfileBaseLookupService extends ReportLookupService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public List<GeographicZone> getAllZones() {
        Facility facility = getCurrentUserFacility();

        if (facility.getFacilityType().is(Central.toString())) {
            return super.getAllZones();
        } else if (facility.getFacilityType().is(DPM.toString())) {
            Long provinceZoneId = facility.getGeographicZone().getParent().getId();
            return geographicZoneMapper.getZoneAndChildren(provinceZoneId);
        } else {
            Long districtZoneId = facility.getGeographicZone().getId();
            return geographicZoneMapper.getZoneAndParent(districtZoneId);
        }
    }

    @Override
    public List<org.openlmis.report.model.dto.Facility> getAllFacilities(RowBounds bounds) {
        Facility facility = getCurrentUserFacility();
        FacilityType facilityType = facility.getFacilityType();

        if (facilityType.is(CSRUR_I.toString()) || facilityType.is(CSRUR_II.toString())) {
            return singletonList(facilityReportMapper.getFacilityByCode(facility.getCode()));
        } else {
            return super.getAllFacilities(bounds);
        }
    }

    public Facility getCurrentUserFacility() {
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession().getAttribute(USER_ID);

        Long facilityId = userRepository.getById(currentUserId).getFacilityId();
        Facility facility = facilityRepository.getById(facilityId);

        return facility;
    }
}
