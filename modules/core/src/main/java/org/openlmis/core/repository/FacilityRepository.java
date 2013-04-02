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

import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Component
@NoArgsConstructor
public class FacilityRepository {

  private FacilityMapper mapper;
  private CommaSeparator commaSeparator;
  private GeographicZoneRepository geographicZoneRepository;
  private static Integer LOWEST_GEO_LEVEL ;

  @Autowired
  public FacilityRepository(FacilityMapper facilityMapper, CommaSeparator commaSeparator, GeographicZoneRepository geographicZoneRepository) {
    this.mapper = facilityMapper;
    this.commaSeparator = commaSeparator;
    this.geographicZoneRepository = geographicZoneRepository;
  }

  public List<Facility> getAll() {
    return mapper.getAll();
  }

  public List<Facility> getAllFacilitiesDetail(){
      return mapper.getAllFacilitiesDetail();
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
    if(LOWEST_GEO_LEVEL == null) {
      LOWEST_GEO_LEVEL = geographicZoneRepository.getLowestGeographicLevel();
    }
    GeographicZone geographicZone = geographicZoneRepository.getByCode(facility.getGeographicZone().getCode());
    facility.setGeographicZone(geographicZone);

    if(facility.getGeographicZone() == null){
      throw new DataException("Invalid reference data 'Geographic Zone Code'");
    }

    if(facility.getGeographicZone().getLevel().getLevelNumber() != LOWEST_GEO_LEVEL){
      throw new DataException("Geographic Zone Code must be at the lowest administrative level in your hierarchy");
    }
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
}
