/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Component
@NoArgsConstructor
public class FacilityRepository {

  private FacilityMapper mapper;
  private CommaSeparator commaSeparator;
  private GeographicZoneRepository geographicZoneRepository;
  private static Integer LOWEST_GEO_LEVEL;

  @Autowired
  public FacilityRepository(FacilityMapper facilityMapper, CommaSeparator commaSeparator, GeographicZoneRepository geographicZoneRepository) {
    this.mapper = facilityMapper;
    this.commaSeparator = commaSeparator;
    this.geographicZoneRepository = geographicZoneRepository;
  }

  public List<Facility> getAll() {
    return mapper.getAll();
  }

  public void save(Facility facility) {
    try {
      validateAndSetFacilityOperator(facility);
      validateAndSetFacilityType(facility);
      validateGeographicZone(facility);
      if (facility.getId() == null) {
        mapper.insert(facility);
      } else {
        mapper.update(facility);
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate Facility Code found");
    } catch (DataIntegrityViolationException integrityViolationException) {
      String errorMessage = integrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("not-null constraint")) {
        throw new DataException("Missing/Invalid Reference data");
      }
      throw new DataException("Incorrect data length");
    }
  }

  private void setFacilityId(Facility savedFacility, Facility facility) {
    if (savedFacility != null) {
      facility.setId(savedFacility.getId());
    }
  }

  private void validateGeographicZone(Facility facility) {
    if (LOWEST_GEO_LEVEL == null) {
      LOWEST_GEO_LEVEL = geographicZoneRepository.getLowestGeographicLevel();
    }
    GeographicZone geographicZone = geographicZoneRepository.getByCode(facility.getGeographicZone().getCode());
    facility.setGeographicZone(geographicZone);

    if (facility.getGeographicZone() == null) {
      throw new DataException("Invalid reference data 'Geographic Zone Code'");
    }

    if (facility.getGeographicZone().getLevel().getLevelNumber() != LOWEST_GEO_LEVEL) {
      throw new DataException("Geographic Zone Code must be at the lowest administrative level in your hierarchy");
    }
  }

  private void validateAndSetFacilityType(Facility facility) {
    FacilityType facilityType = facility.getFacilityType();
    if (facilityType == null || facilityType.getCode() == null || facilityType.getCode().isEmpty())
      throw new DataException("Missing mandatory reference data 'Facility Type'");

    String facilityTypeCode = facilityType.getCode();
    FacilityType existingFacilityType = mapper.getFacilityTypeForCode(facilityTypeCode);

    if (existingFacilityType == null)
      throw new DataException("Invalid reference data 'Facility Type'");

    facilityType.setId(existingFacilityType.getId());

  }

  private void validateAndSetFacilityOperator(Facility facility) {
    if (facility.getOperatedBy() == null) return;

    String operatedByCode = facility.getOperatedBy().getCode();
    if (operatedByCode == null || operatedByCode.isEmpty()) return;

    Integer operatedById = mapper.getOperatedByIdForCode(operatedByCode);
    if (operatedById == null) throw new DataException("Invalid reference data 'Operated By'");

    facility.getOperatedBy().setId(operatedById);
  }

  public List<FacilityType> getAllTypes() {
    return mapper.getAllTypes();
  }

  public List<FacilityOperator> getAllOperators() {
    return mapper.getAllOperators();
  }

  public Facility getHomeFacility(Integer userId) {
    return mapper.getHomeFacility(userId);
  }

  public Facility getById(Integer id) {
    return mapper.getById(id);
  }

  public void updateDataReportableAndActiveFor(Facility facility) {
    mapper.updateDataReportableAndActiveFor(facility);

  }

  public List<Facility> getFacilitiesBy(Integer programId, List<RequisitionGroup> requisitionGroups) {
    return mapper.getFacilitiesBy(programId, commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public List<Facility> getAllInRequisitionGroups(List<RequisitionGroup> requisitionGroups) {
    return mapper.getAllInRequisitionGroups(commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public Integer getIdForCode(String code) {
    Integer facilityId = mapper.getIdForCode(code);

    if (facilityId == null)
      throw new DataException("Invalid Facility Code");

    return facilityId;
  }

  public List<Facility> searchFacilitiesByCodeOrName(String searchParam) {
    return mapper.searchFacilitiesByCodeOrName(searchParam);
  }

  public Facility getHomeFacilityForRights(Integer userId, Right... rights) {
    return mapper.getHomeFacilityWithRights(userId, commaSeparateRightNames(rights));
  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    return mapper.getFacilityTypeForCode(facilityType.getCode());
  }

  public Facility getByCode(Facility facility) {
    return mapper.getByCode(facility.getCode());
  }

}
