package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacilityRepository {

    private FacilityMapper facilityMapper;

    @Autowired
    public FacilityRepository(FacilityMapper facilityMapper) {
        this.facilityMapper = facilityMapper;
    }

    public List<Facility> getAll() {
        return facilityMapper.getAll();
    }

    public RequisitionHeader getHeader(String facilityCode) {
        return facilityMapper.getRequisitionHeaderData(facilityCode);
    }

    public void save(Facility facility) {
        try {
            facility.setModifiedDate(DateTime.now().toDate());
            facilityMapper.insert(facility);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException("Duplicate Facility Code found");
        } catch (DataIntegrityViolationException integrityViolationException) {
            if (integrityViolationException.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException("Missing Reference data");
            }
        }
    }
}
