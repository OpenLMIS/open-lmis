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

    private SupervisoryNodeService supervisoryNodeService;
    private RequisitionGroupService requisitionGroupService;


    @Autowired
    public FacilityService(FacilityRepository facilityRepository, SupervisoryNodeService supervisoryNodeService, RequisitionGroupService requisitionGroupService) {

        this.facilityRepository = facilityRepository;
        this.supervisoryNodeService = supervisoryNodeService;
        this.requisitionGroupService = requisitionGroupService;
    }

    public List<Facility> getAll() {
        return facilityRepository.getAll();
    }

    public RequisitionHeader getRequisitionHeader(Integer facilityId) {
        return facilityRepository.getHeader(facilityId);
    }


    public void save(Facility facility) {
        facilityRepository.save(facility);
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

    public List<Facility> getAllForUser(Integer userId) {
        Facility homeFacility = facilityRepository.getHomeFacility(userId);
        return homeFacility == null ? Collections.<Facility>emptyList() : Arrays.asList(homeFacility);
    }

    public Facility getById(Integer id) {
        return facilityRepository.getById(id);
    }

    public void updateDataReportableAndActiveFor(Facility facility) {
        facilityRepository.updateDataReportableAndActiveFor(facility);
    }

    public List<Facility> getUserSupervisedFacilities(Integer userId, Integer programId, Right... rights) {
        List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, rights);
        List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);
        return facilityRepository.getFacilitiesBy(programId, requisitionGroups);
    }
}
