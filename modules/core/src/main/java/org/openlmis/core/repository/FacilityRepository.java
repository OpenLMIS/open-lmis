package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityRepository {

  private FacilityMapper mapper;
  private CommaSeparator commaSeparator;

  @Autowired
  public FacilityRepository(FacilityMapper facilityMapper, CommaSeparator commaSeparator) {
    this.mapper = facilityMapper;
    this.commaSeparator = commaSeparator;
  }

  public List<Facility> getAll() {
    return mapper.getAll();
  }

  public void save(Facility facility) {
    facility.setModifiedDate(DateTime.now().toDate());
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

  private void validateGeographicZone(Facility facility) {
    GeographicZone geographicZone = facility.getGeographicZone();

    if (geographicZone == null || geographicZone.getId() == null)
      throw new DataException("Missing mandatory reference data 'Geographic Zone Id'");

    Integer geographicZoneId = geographicZone.getId();
    Boolean geographicZonePresent = mapper.isGeographicZonePresent(geographicZoneId);

    if (!geographicZonePresent)
      throw new DataException("Invalid reference data 'Geographic Zone Id'");
  }

  private void validateAndSetFacilityType(Facility facility) {
    FacilityType facilityType = facility.getFacilityType();
    if (facilityType == null || facilityType.getCode() == null || facilityType.getCode().isEmpty())
      throw new DataException("Missing mandatory reference data 'Facility Type'");

    String facilityTypeCode = facilityType.getCode();
    Integer facilityTypeId = mapper.getFacilityTypeIdForCode(facilityTypeCode);

    if (facilityTypeId == null)
      throw new DataException("Invalid reference data 'Facility Type'");

    facilityType.setId(facilityTypeId);

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

  public List<GeographicZone> getAllGeographicZones() {
    return mapper.getAllGeographicZones();
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

  public Integer getIdForCode(String code) {
    Integer facilityId = mapper.getIdForCode(code);

    if (facilityId == null)
      throw new DataException("Invalid Facility Code");

    return facilityId;
  }

  public List<Facility> searchFacilitiesByCodeOrName(String searchParam) {
    return mapper.searchFacilitiesByCodeOrName(searchParam);
  }

  public void insert(Facility facility) {
     mapper.insert(facility);
  }

  public void update(Facility facility) {
    //To change body of created methods use File | Settings | File Templates.
  }
}
