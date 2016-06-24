package org.openlmis.report.service.lookup;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.report.model.dto.GeographicZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.moz.MozFacilityTypes.DPM;

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

        if (facility.getFacilityType().is(DPM.toString())) {
            Long provinceZoneId = facility.getGeographicZone().getParent().getId();
            return geographicZoneMapper.getZoneAndChildren(provinceZoneId);
        } else {
            return super.getAllZones();
        }
    }

    private Facility getCurrentUserFacility() {
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession().getAttribute(USER_ID);

        Long facilityId = userRepository.getById(currentUserId).getFacilityId();
        Facility facility = facilityRepository.getById(facilityId);

        return facility;
    }
}
