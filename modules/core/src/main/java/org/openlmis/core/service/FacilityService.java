package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@NoArgsConstructor
public class FacilityService {

  private FacilityRepository facilityRepository;
  private ProgramSupportedRepository programSupportedRepository;

  private ProgramRepository programRepository;
  private SupervisoryNodeService supervisoryNodeService;
  private RequisitionGroupService requisitionGroupService;


  @Autowired
  public FacilityService(FacilityRepository facilityRepository, ProgramSupportedRepository programSupportedRepository, ProgramRepository programRepository, SupervisoryNodeService supervisoryNodeService, RequisitionGroupService requisitionGroupService) {
    this.facilityRepository = facilityRepository;
    this.programSupportedRepository = programSupportedRepository;
    this.programRepository = programRepository;
    this.supervisoryNodeService = supervisoryNodeService;
    this.requisitionGroupService = requisitionGroupService;
  }

  public List<Facility> getAll() {
    return facilityRepository.getAll();
  }

  public void save(Facility facility) {
    facilityRepository.save(facility);
    saveSupportedPrograms(facility);
  }

  private void saveSupportedPrograms(Facility facility) {
    if (facility.getId() == null) {
      programSupportedRepository.addSupportedProgramsFor(facility);
    } else {
      programSupportedRepository.updateSupportedPrograms(facility, programSupportedRepository.getAllByFacilityId(facility.getId()));
    }
  }

  public void uploadSupportedProgram(ProgramSupported programSupported) {
    if (programSupported.getActive() && programSupported.getStartDate() == null)
      throw new DataException("Start date is a must for Active program");

    programSupported.setFacilityId(facilityRepository.getIdForCode(programSupported.getFacilityCode()));
    programSupported.setProgramId(programRepository.getIdByCode(programSupported.getProgramCode()));
    programSupportedRepository.addSupportedProgram(programSupported);
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
    Facility facility = facilityRepository.getById(id);
    facility.setSupportedPrograms(programSupportedRepository.getAllByFacilityId(id));
    return facility;
  }

  public void updateDataReportableAndActiveFor(Facility facility) {
    facilityRepository.updateDataReportableAndActiveFor(facility);
  }

  public List<Facility> getUserSupervisedFacilities(Integer userId, Integer programId, Right... rights) {
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, rights);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);
    return facilityRepository.getFacilitiesBy(programId, requisitionGroups);
  }

  public List<Facility> searchFacilitiesByCodeOrName(String searchParam) {
    return facilityRepository.searchFacilitiesByCodeOrName(searchParam);
  }

  public void insert(Facility facility) {
    facilityRepository.insert(facility);
    programSupportedRepository.addSupportedProgramsFor(facility);
  }

  public void update(Facility facility) {
    facilityRepository.update(facility);
  }
}
