package org.openlmis.core.repository;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacilityRepository {

    @Autowired
    private FacilityMapper facilityMapper;

    public List<Facility> getAll() {
        return facilityMapper.getAll();
    }

}
