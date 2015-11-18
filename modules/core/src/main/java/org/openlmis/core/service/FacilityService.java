/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.select;

/**
 * Exposes the services for handling Facility entity.
 */

@Service
@NoArgsConstructor
public class FacilityService {

  public static final String FACILITY_CATEGORY = "facilities";
  public static final String FACILITY_TITLE = "Facility";
  public static final String ERROR_FACILITY_CODE_INVALID = "error.facility.code.invalid";
  public static final String SMS = "SMS";
  public static final String EMAIL = "EMAIL";

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProgramSupportedService programSupportedService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private GeographicZoneRepository geographicZoneRepository;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private ELMISInterfaceService elmisInterfaceService;

  @Autowired
  private EventService eventService;

  private static final Logger LOGGER = Logger.getLogger(FacilityService.class);

  @Transactional
  public void update(Facility facility) {
    save(facility);
    programSupportedService.updateSupportedPrograms(facility);
    elmisInterfaceService.updateFacilityInterfaceMapping(facility);
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

  public Facility getHomeFacility(Long userId) {
    return facilityRepository.getHomeFacility(userId);
  }

  public Facility getById(Long id) {
    Facility facility = facilityRepository.getById(id);
    if(facility != null)
    {
      facility.setSupportedPrograms(programSupportedService.getAllByFacilityId(id));
      facility.setInterfaceMappings(elmisInterfaceService.getFacilityInterfaceMappingById(id));
    }
    return facility;
  }

  @Transactional
  public void updateEnabledAndActiveFor(Facility facility) {
    facility = facilityRepository.updateEnabledAndActiveFor(facility);
    notify(asList(facility));
  }

  public List<Facility> getUserSupervisedFacilities(Long userId, Long programId, String... rightNames) {
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, rightNames);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);
    return facilityRepository.getFacilitiesBy(programId, requisitionGroups);
  }

  public void save(Facility newFacility) {
    newFacility.validate();

    Facility storedFacility = facilityRepository.getById(newFacility.getId());

    facilityRepository.save(newFacility);

    if (!newFacility.equals(storedFacility)) {
      notify(asList(newFacility));
      if (canUpdateVirtualFacilities(newFacility, storedFacility)) {
        updateAndNotifyForVirtualFacilities(newFacility);
      }
    }
  }

  public void updateAndNotifyForVirtualFacilities(Facility parentFacility) {
    facilityRepository.updateVirtualFacilities(parentFacility);
    notify(getChildFacilities(parentFacility));
  }

  private boolean canUpdateVirtualFacilities(Facility newFacility, Facility oldFacility) {
    return (oldFacility == null ||
        !(newFacility.getGeographicZone().getCode().equals(oldFacility.getGeographicZone().getCode())) ||
        !(newFacility.getFacilityType().getCode().equals(oldFacility.getFacilityType().getCode()))
    );
  }

  private void notify(List<Facility> facilities) {
    //TODO newFacility doesn't have modifiedDate populated
    for (Facility facility : facilities) {
      try {
        Facility parentFacility = facilityRepository.getById(facility.getParentFacilityId());
        FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility, parentFacility);
        String content = facilityFeedDTO.getSerializedContents();
        eventService.notify(new Event(UUID.randomUUID().toString(), FACILITY_TITLE, DateTime.now(), "", content, FACILITY_CATEGORY));
      } catch (URISyntaxException e) {
        LOGGER.error("Unable to generate facility event", e);
      }
    }
  }

  public List<Facility> getForUserAndRights(Long userId, String... rightNames) {
    List<SupervisoryNode> supervisoryNodesInHierarchy = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, rightNames);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodesInHierarchy);
    final Set<Facility> userFacilities = new HashSet<>(facilityRepository.getAllInRequisitionGroups(requisitionGroups));
    final Facility homeFacility = facilityRepository.getHomeFacilityForRights(userId, rightNames);

    if (homeFacility != null) userFacilities.add(homeFacility);

    return new ArrayList<>(userFacilities);

  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    return facilityRepository.getFacilityTypeByCode(facilityType);
  }

  public Facility getByCode(Facility facility) {
    return facilityRepository.getByCode(facility.getCode());
  }

  public List<Facility> getAllForDeliveryZoneAndProgram(Long deliveryZoneId, Long programId) {
    List<Facility> facilities = facilityRepository.getAllInDeliveryZoneFor(deliveryZoneId, programId);
    for (Facility facility : facilities) {
      facility.getSupportedPrograms().add(programSupportedService.getFilledByFacilityIdAndProgramId(facility.getId(), programId));
    }
    return facilities;
  }

  public List<Facility> getAllByProgramSupportedModifiedDate(Date dateModified) {
    return facilityRepository.getAllByProgramSupportedModifiedDate(dateModified);
  }

  public Facility getFacilityWithReferenceDataForCode(String facilityCode) {
    Long facilityId = facilityRepository.getIdForCode(facilityCode);
    return getById(facilityId);
  }

  public List<Facility> searchBy(String searchParam, String columnName, Pagination pagination) {
    return facilityRepository.searchBy(searchParam, columnName, pagination);
  }

  public List<Facility> getEnabledWarehouses() {
    return facilityRepository.getEnabledWarehouses();
  }

  public Facility getFacilityByCode(String facilityCode) {
    Facility facility;
    if ((facility = facilityRepository.getByCode(facilityCode)) == null) {
      throw new DataException(ERROR_FACILITY_CODE_INVALID);
    }

    facility.setSupportedPrograms((List<ProgramSupported>) select(facility.getSupportedPrograms(), new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((ProgramSupported) o).getActive();
      }
    }));

    return facility;
  }

  public List<Facility> getChildFacilities(Facility facility) {
    return facilityRepository.getChildFacilities(facility);
  }

  public List<Facility> getAllByRequisitionGroupMemberModifiedDate(Date modifiedDate) {
    return facilityRepository.getAllByRequisitionGroupMemberModifiedDate(modifiedDate);
  }

  public List<Facility> getAllParentsByModifiedDate(Date modifiedDate) {
    return facilityRepository.getAllParentsByModifiedDate(modifiedDate);
  }

  public Facility getOperativeFacilityByCode(String facilityCode) {
    Facility facility = getFacilityByCode(facilityCode);

    Facility parentFacility = null;
    if (facility.getVirtualFacility()) {
      parentFacility = facilityRepository.getById(facility.getParentFacilityId());
    }

    if (!facility.isValid(parentFacility)) {
      throw new DataException("error.facility.inoperative");
    }

    return facility;
  }

  public List<Facility> getCompleteListInRequisitionGroups(List<RequisitionGroup> requisitionGroups) {
    return facilityRepository.getAllInRequisitionGroups(requisitionGroups);
  }

  public List<FacilityContact> getContactList(Long facilityId, String notificationMedium) {
    if (SMS.equalsIgnoreCase(notificationMedium)) {
      return facilityRepository.getSmsContacts(facilityId);
    }
    if (EMAIL.equalsIgnoreCase(notificationMedium)) {
      return facilityRepository.getEmailContacts(facilityId);
    }
    return null;
  }

  public List<FacilitySupervisor> getFacilitySupervisors(Long facilityId) {
    return facilityRepository.getFacilitySupervisors(facilityId);
  }

  public List<FacilityImages> getFacilityImages(Long facilityId) {
    return facilityRepository.getFacilityImages(facilityId);
  }

  public List<Facility> getUserSupervisedFacilities(Long userId) {
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);
    return facilityRepository.getAllInRequisitionGroups(requisitionGroups);
  }

  public List<Facility> getAllForGeographicZone(Long geographizZoneId) {
    return facilityRepository.getAllForGeographicZone(geographizZoneId);
  }

  public Integer getFacilitiesCountBy(String searchParam, Long facilityTypeId, Long geoZoneId, Boolean virtualFacility, Boolean enabled) {
    return facilityRepository.getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  public List<Facility> searchFacilitiesBy(String searchParam, Long facilityTypeId, Long geoZoneId, Boolean virtualFacility, Boolean enabled) {
    return facilityRepository.searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  public Integer getTotalSearchResultCountByColumnName(String searchParam, String columnName) {
    if (columnName.equalsIgnoreCase("Facility")) {
      return facilityRepository.getTotalSearchResultCount(searchParam);
    }
    return facilityRepository.getTotalSearchResultCountByGeographicZone(searchParam);
  }

  public List<Facility> getFacilityByTypeAndRequisitionGroupId(Long facilityTypeId, Long rgroupId) {
    return facilityRepository.getFacilityByTypeAndRequisitionGroupId(facilityTypeId, rgroupId);
  }

  public List<FacilityGeoTreeDto> getGeoRegionFacilityTree(Long userId) {
    return facilityRepository.getGeoRegionFacilityTree(userId);
  }

  public List<FacilityGeoTreeDto> getGeoDistrictFacility(Long userId) {
    return facilityRepository.getGeoDistrictFacility(userId);
  }

  public List<FacilityGeoTreeDto> getGeoFlatFacilityTree(Long userId) {
    return facilityRepository.getGeoFlatFacilityTree(userId);
  }

  public Facility getFacilityById(Long id) {
    Facility facility = facilityRepository.getById(id);
    facility.setSupportedPrograms(programSupportedService.getAllByFacilityId(id));
    return facility;
  }
}
