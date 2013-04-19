/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class FacilityService {

  private FacilityRepository facilityRepository;
  private ProgramSupportedRepository programSupportedRepository;

  private ProgramRepository programRepository;
  private RequisitionGroupService requisitionGroupService;
  private GeographicZoneRepository geographicZoneRepository;
  private SupervisoryNodeService supervisoryNodeService;

  public static final String SUPPORTED_PROGRAMS_INVALID = "supported.programs.invalid";

  @Autowired
  public FacilityService(FacilityRepository facilityRepository, ProgramSupportedRepository programSupportedRepository,
                         ProgramRepository programRepository, SupervisoryNodeService supervisoryNodeService,
                         RequisitionGroupService requisitionGroupService, GeographicZoneRepository geographicZoneRepository) {
    this.facilityRepository = facilityRepository;
    this.programSupportedRepository = programSupportedRepository;
    this.programRepository = programRepository;
    this.supervisoryNodeService = supervisoryNodeService;
    this.requisitionGroupService = requisitionGroupService;
    this.geographicZoneRepository = geographicZoneRepository;
  }

  @Transactional
  public void insert(Facility facility) {
    save(facility);
    programSupportedRepository.addSupportedProgramsFor(facility);
  }

  @Transactional
  public void update(Facility facility) {
    save(facility);
    programSupportedRepository.updateSupportedPrograms(facility, programSupportedRepository.getAllByFacilityId(facility.getId()));
  }

  public List<Facility> getAll() {
    return facilityRepository.getAll();
  }

  public List<Facility> getAllFacilitiesDetail(){
    return facilityRepository.getAllFacilitiesDetail();
  }

  public List<Facility> getMailingLabels(){
      return facilityRepository.getMailingLabels();
  }

  public void uploadSupportedProgram(ProgramSupported programSupported) {
    programSupported.isValid();

    Integer facilityId = facilityRepository.getIdForCode(programSupported.getFacilityCode());
    programSupported.setFacilityId(facilityId);
    Integer programId = programRepository.getIdByCode(programSupported.getProgram().getCode());
    programSupported.setProgram(new Program(programId));

    if (programSupported.getId() == null) {
      programSupportedRepository.addSupportedProgram(programSupported);
    }
    else{
      programSupportedRepository.updateSupportedProgram(programSupported);
    }
  }

  public List<FacilityType> getAllTypes() {
    return facilityRepository.getAllTypes();
  }

  public List<FacilityOperator> getAllOperators() {
    return facilityRepository.getAllOperators();
  }

  public List<GeographicZone> getAllZones() {
    return geographicZoneRepository.getAllGeographicZones();
  }

  public List<GeographicZone> getAllGeographicZones() {
        return geographicZoneRepository.getAllZones();
  }


  public Facility getHomeFacility(Integer userId) {
    return facilityRepository.getHomeFacility(userId);
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

  public void save(Facility facility) {
    for (ProgramSupported programSupported : facility.getSupportedPrograms()) {
      programSupported.isValid();
    }
    facilityRepository.save(facility);
  }

  public List<Facility> getForUserAndRights(Integer userId, Right... rights) {
    List<SupervisoryNode> supervisoryNodesInHierarchy = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, rights);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodesInHierarchy);
    final Set<Facility> userFacilities = new HashSet<>(facilityRepository.getAllInRequisitionGroups(requisitionGroups));
    final Facility homeFacility = facilityRepository.getHomeFacilityForRights(userId, rights);

    if(homeFacility!=null) userFacilities.add(homeFacility);

    return new ArrayList<>(userFacilities);

  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    return facilityRepository.getFacilityTypeByCode(facilityType);
  }

  public Facility getByCode(Facility facility) {
    return facilityRepository.getByCode(facility);
  }

  public ProgramSupported getProgramSupported(ProgramSupported programSupported) {
    Integer facilityId = facilityRepository.getIdForCode(programSupported.getFacilityCode());
    Integer programId = programRepository.getIdByCode(programSupported.getProgram().getCode());

    return programSupportedRepository.getByFacilityIdAndProgramId(facilityId, programId);
  }
}
