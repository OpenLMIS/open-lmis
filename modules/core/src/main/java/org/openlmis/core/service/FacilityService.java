package org.openlmis.core.service;


import org.openlmis.core.dao.FacilityMapper;
import org.openlmis.core.domain.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService {

    private FacilityMapper facilityMapper;

    @Autowired
    public FacilityService(FacilityMapper facilityMapper) {
        this.facilityMapper = facilityMapper;
    }

    public List<Facility> getAll() {
        return facilityMapper.getAll();
    }

}
