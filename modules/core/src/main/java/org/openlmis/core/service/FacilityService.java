package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@NoArgsConstructor
public class FacilityService {

    private FacilityRepository facilityRepository;

    @Autowired
    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAll() {
        return facilityRepository.getAll();
    }

    public RequisitionHeader getRequisitionHeader(Long facilityId) {
        return facilityRepository.getHeader(facilityId);
    }


    public void save(Facility facility) {
        facilityRepository.save(facility);
    }

    public void addSupportedProgram(ProgramSupported programSupported) {
        facilityRepository.addSupportedProgram(programSupported);
    }

    public List<FacilityType> getAllTypes() {
        return facilityRepository.getAllTypes();
    }

    public List<FacilityOperator> getAllOperators() {
        return facilityRepository.getAllOperators();
    }

    public List<GeographicZone> getAllZones() {
        return facilityRepository.getAllGeographicZones();
    }

    //TODO this will also return facilities based on requisition group
    public List<Facility> getAllForUser(String user) {
        Facility homeFacility = facilityRepository.getHomeFacility(user);
        return homeFacility == null ? Collections.<Facility>emptyList() : Arrays.asList(homeFacility);
    }

    public Facility getFacility(Long id) {
        return facilityRepository.getFacility(id);
    }

    public void updateDataReportableAndActiveFor(Facility facility) {
        facilityRepository.updateDataReportableAndActiveFor(facility);
    }
}
