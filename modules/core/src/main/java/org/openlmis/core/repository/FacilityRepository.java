/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityContact;
import org.openlmis.core.dto.FacilityGeoTreeDto;
import org.openlmis.core.dto.FacilityImages;
import org.openlmis.core.dto.FacilitySupervisor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.service.PriceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.RightName.commaSeparateRightNames;

/**
 * FacilityRepository is repository class for Facility related database operations.
 */

@Component
@NoArgsConstructor
public class FacilityRepository {

  @Autowired
  private FacilityMapper mapper;

  @Autowired
  private CommaSeparator commaSeparator;

  @Autowired
  private GeographicZoneRepository geographicZoneRepository;

  @Autowired
  private PriceScheduleService priceScheduleService;
  
  public List<Facility> getMailingLabels(){
    return mapper.getMailingLabels();
  }
  
  public void save(Facility facility) {
    try {
      validateAndSetFacilityOperatedBy(facility);
      validateAndSetFacilityType(facility);
      validateGeographicZone(facility);
      validateEnabledAndActive(facility);
      validateAndSetPriceScheduleCategory(facility);
      if (facility.getId() == null) {
        mapper.insert(facility);
      } else {
        mapper.update(facility);
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.facility.code");
    } catch (DataIntegrityViolationException integrityViolationException) {
      String errorMessage = integrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      }
      throw new DataException("error.incorrect.length");
    }
  }

    private void validateAndSetPriceScheduleCategory(Facility facility) {

        PriceSchedule priceSchedule = facility.getPriceSchedule();
        if (priceSchedule == null || priceSchedule.getId() != null)
          return;

        priceSchedule = priceScheduleService.getByCode(facility.getPriceSchedule().getCode());
        facility.setPriceSchedule(priceSchedule);
    }

    private void validateEnabledAndActive(Facility facility) {
    if (facility.getEnabled() == Boolean.FALSE && facility.getActive() == Boolean.TRUE)
      throw new DataException("error.enabled.false");
  }

  private void validateGeographicZone(Facility facility) {
    Integer lowestGeographicLevel = geographicZoneRepository.getLowestGeographicLevel();
    GeographicZone geographicZone = geographicZoneRepository.getByCode(facility.getGeographicZone().getCode());
    facility.setGeographicZone(geographicZone);

    if (facility.getGeographicZone() == null) {
      throw new DataException("error.reference.data.invalid.geo.zone.code");
    }

    if (!facility.getGeographicZone().getLevel().getLevelNumber().equals(lowestGeographicLevel)) {
      throw new DataException("error.geo.zone.not.at.lowest.level");
    }
  }

  private void validateAndSetFacilityType(Facility facility) {
    FacilityType facilityType = facility.getFacilityType();
    if (facilityType == null || facilityType.getCode() == null || facilityType.getCode().isEmpty())
      throw new DataException("error.reference.data.facility.type.missing");

    String facilityTypeCode = facilityType.getCode();
    FacilityType existingFacilityType = mapper.getFacilityTypeForCode(facilityTypeCode);

    if (existingFacilityType == null)
      throw new DataException("error.reference.data.invalid.facility.type");

    facility.setFacilityType(existingFacilityType);
  }

  private void validateAndSetFacilityOperatedBy(Facility facility) {
    if (facility.getOperatedBy() == null) return;

    String operatedByCode = facility.getOperatedBy().getCode();
    if (operatedByCode == null || operatedByCode.isEmpty()) return;

    Long operatedById = mapper.getOperatedByIdForCode(operatedByCode);
    if (operatedById == null)
      throw new DataException("error.reference.data.invalid.operated.by");

    facility.setOperatedBy(mapper.getFacilityOperatorById(operatedById));
  }

  public List<FacilityType> getAllTypes() {
    return mapper.getAllTypes();
  }

  public List<FacilityOperator> getAllOperators() {
    return mapper.getAllOperators();
  }

  public Facility getHomeFacility(Long userId) {
    return mapper.getHomeFacility(userId);
  }

  public Facility getById(Long id) {
    return mapper.getById(id);
  }

  public Facility updateEnabledAndActiveFor(Facility facility) {
    mapper.updateEnabledAndActiveFor(facility);
    //TODO is this required??
    return mapper.getById(facility.getId());
  }

  public List<Facility> getFacilitiesBy(Long programId, List<RequisitionGroup> requisitionGroups) {
    return mapper.getFacilitiesBy(programId, commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public List<Facility> getAllInRequisitionGroups(List<RequisitionGroup> requisitionGroups) {
    return mapper.getAllInRequisitionGroups(commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public List<Facility> getAllByFacilityTypeCode(String typeCode) {
    return mapper.getAllByFacilityTypeCode(typeCode);
  }

  public Long getIdForCode(String code) {
    Long facilityId = mapper.getIdForCode(code);

    if (facilityId == null)
      throw new DataException("error.facility.code.invalid");

    return facilityId;
  }

  public Facility getHomeFacilityForRights(Long userId, String... rightNames) {
    return mapper.getHomeFacilityWithRights(userId, commaSeparateRightNames(rightNames));
  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    facilityType = mapper.getFacilityTypeForCode(facilityType.getCode());
    if (facilityType == null) {
      throw new DataException("error.facility.type.code.invalid");
    }
    return facilityType;
  }

  public Facility getByCode(String code) {
    return mapper.getByCode(code);
  }

  public List<Facility> getAllInDeliveryZoneFor(Long deliveryZoneId, Long programId) {
    return mapper.getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }

  public List<Facility> getAllByProgramSupportedModifiedDate(Date dateModified) {
    return mapper.getAllByProgramSupportedModifiedDate(dateModified);
  }

  public List<Facility> getEnabledWarehouses() {
    return mapper.getEnabledWarehouses();
  }

  public List<Facility> getChildFacilities(Facility facility) {
    return mapper.getChildFacilities(facility);
  }

  public void updateVirtualFacilities(Facility parentFacility) {
    mapper.updateVirtualFacilities(parentFacility);
  }

  public List<Facility> getAllByRequisitionGroupMemberModifiedDate(Date modifiedDate) {
    return mapper.getAllByRequisitionGroupMemberModifiedDate(modifiedDate);
  }

  public List<Facility> getAllParentsByModifiedDate(Date modifiedDate) {
    return mapper.getAllParentsByModifiedDate(modifiedDate);
  }

  public Integer getFacilitiesCountBy(String searchParam, Long facilityTypeId, Long geoZoneId, Boolean virtualFacility, Boolean enabled) {
    return mapper.getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  public List<Facility> searchFacilitiesBy(String searchParam, Long facilityTypeId, Long geoZoneId, Boolean virtualFacility, Boolean enabled) {
    return mapper.searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
  }

  public Integer getTotalSearchResultCount(String searchParam) {
    return mapper.getTotalSearchResultCount(searchParam);
  }

  public Integer getTotalSearchResultCountByGeographicZone(String searchParam) {
    return mapper.getTotalSearchResultCountByGeographicZone(searchParam);
  }

  public List<Facility> searchBy(String searchParam, String columnName, Pagination pagination) {
    return mapper.search(searchParam,columnName,pagination);
  }

  public List<FacilityContact> getEmailContacts(Long facilityId) {
    return mapper.getEmailContacts(facilityId);
  }

  public List<FacilityContact> getSmsContacts(Long facilityId) {
    return mapper.getSmsContacts(facilityId);
  }

  public List<FacilitySupervisor> getFacilitySupervisors(Long facilityId) {  return mapper.getFacilitySupervisors(facilityId); }

  public List<FacilityImages> getFacilityImages(Long facilityId) {
    return mapper.getFacilityImages(facilityId);
  }

  public List<Facility> getAllForGeographicZone(Long geographicZoneId){
      return mapper.getForGeographicZone(geographicZoneId);
  }
  public List<Facility> getFacilityByTypeAndRequisitionGroupId(Long facilityTypeId, Long rgroupId){
      return mapper.getFacilitiesByTypeAndRequisitionGroupId(facilityTypeId, rgroupId);
  }

    public List<FacilityGeoTreeDto> getGeoRegionFacilityTree(Long userId) {
        return mapper.getGeoRegionFacilityTree(userId);
    }

    public List<FacilityGeoTreeDto> getGeoDistrictFacility(Long userId)  {
        return mapper.getGeoTreeDistricts(userId);
    }

    public List<FacilityGeoTreeDto> getGeoFlatFacilityTree(Long userId) {   return mapper.getGeoTreeFlatFacilities(userId);  }
}
