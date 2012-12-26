package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityRepository {

    private FacilityMapper facilityMapper;
    private ProgramSupportedMapper programSupportedMapper;
    private ProgramMapper programMapper;
    private CommaSeparator commaSeparator;

    @Autowired
    public FacilityRepository(FacilityMapper facilityMapper, ProgramSupportedMapper programSupportedMapper,
                              ProgramMapper programMapper, CommaSeparator commaSeparator) {
        this.facilityMapper = facilityMapper;
        this.programSupportedMapper = programSupportedMapper;
        this.programMapper = programMapper;
        this.commaSeparator = commaSeparator;
    }

    public List<Facility> getAll() {
        return facilityMapper.getAll();
    }

    public RequisitionHeader getHeader(Integer facilityId) {
        return facilityMapper.getRequisitionHeaderData(facilityId);
    }

    @Transactional
    public void save(Facility facility) {
        facility.setModifiedDate(DateTime.now().toDate());
        try {
            validateAndSetFacilityOperator(facility);
            validateAndSetFacilityType(facility);
            validateGeographicZone(facility);
            if (facility.getId() == null) {
                facilityMapper.insert(facility);
                addListOfSupportedPrograms(facility);
            } else {
                updateFacility(facility);
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
        Boolean geographicZonePresent = facilityMapper.isGeographicZonePresent(geographicZoneId);

        if (!geographicZonePresent)
            throw new DataException("Invalid reference data 'Geographic Zone Id'");
    }

    private void validateAndSetFacilityType(Facility facility) {
        FacilityType facilityType = facility.getFacilityType();
        if (facilityType == null || facilityType.getCode() == null || facilityType.getCode().isEmpty())
            throw new DataException("Missing mandatory reference data 'Facility Type'");

        String facilityTypeCode = facilityType.getCode();
        Integer facilityTypeId = facilityMapper.getFacilityTypeIdForCode(facilityTypeCode);

        if (facilityTypeId == null)
            throw new DataException("Invalid reference data 'Facility Type'");

        facilityType.setId(facilityTypeId);

    }

    private void validateAndSetFacilityOperator(Facility facility) {
        if (facility.getOperatedBy() == null) return;

        String operatedByCode = facility.getOperatedBy().getCode();
        if (operatedByCode == null || operatedByCode.isEmpty()) return;

        Integer operatedById = facilityMapper.getOperatedByIdForCode(operatedByCode);
        if (operatedById == null) throw new DataException("Invalid reference data 'Operated By'");

        facility.getOperatedBy().setId(operatedById);
    }

    private void updateFacility(Facility facility) {
        List<Program> previouslySupportedPrograms = programMapper.getByFacilityId(facility.getId());
        facilityMapper.update(facility);
        deleteObsoleteProgramMappings(facility, previouslySupportedPrograms);
        addUpdatableProgramMappings(facility, previouslySupportedPrograms);
    }

    private void deleteObsoleteProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
        List<Program> supportedPrograms = facility.getSupportedPrograms();
        for (Program previouslySupportedProgram : previouslySupportedPrograms) {
            if (!(supportedPrograms).contains(previouslySupportedProgram)) {
                programSupportedMapper.delete(facility.getId(), previouslySupportedProgram.getId());
            }
        }
    }

    private void addUpdatableProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
        for (Program supportedProgram : facility.getSupportedPrograms()) {
            if (!(previouslySupportedPrograms).contains(supportedProgram)) {
                ProgramSupported newProgramsSupported = new ProgramSupported(facility.getId(), supportedProgram.getId(),
                        supportedProgram.getActive(), facility.getModifiedBy(), facility.getModifiedDate());
                insertSupportedProgram(newProgramsSupported);
            }
        }
    }

    private void addListOfSupportedPrograms(Facility facility) {
        List<Program> supportedPrograms = facility.getSupportedPrograms();
        for (Program supportedProgram : supportedPrograms) {
            ProgramSupported programSupported = new ProgramSupported(facility.getId(), supportedProgram.getId(), supportedProgram.getActive(), facility.getModifiedBy(), facility.getModifiedDate());
            insertSupportedProgram(programSupported);
        }
    }

    private void insertSupportedProgram(ProgramSupported programSupported) {
        try {
            programSupported.setModifiedDate(DateTime.now().toDate());
            programSupportedMapper.addSupportedProgram(programSupported);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("Facility has already been mapped to the program");
        } catch (DataIntegrityViolationException integrityViolationException) {
            throw new DataException("Invalid reference data 'Program Code'");
        }
    }

    public void addSupportedProgram(ProgramSupported programSupported) {
        programSupported.setFacilityId(facilityMapper.getIdForCode(programSupported.getFacilityCode()));
        insertSupportedProgram(programSupported);
    }

    public List<FacilityType> getAllTypes() {
        return facilityMapper.getAllTypes();
    }

    public List<FacilityOperator> getAllOperators() {
        return facilityMapper.getAllOperators();
    }

    public List<GeographicZone> getAllGeographicZones() {
        return facilityMapper.getAllGeographicZones();
    }

    public Facility getHomeFacility(String user) {
        return facilityMapper.getHomeFacility(user);
    }

    public Facility getFacility(Integer id) {
        Facility facility = facilityMapper.get(id);
        facility.setSupportedPrograms(programMapper.getByFacilityId(facility.getId()));
        return facility;
    }

    public void updateDataReportableAndActiveFor(Facility facility) {
        facilityMapper.updateDataReportableAndActiveFor(facility);

    }


    public List<Facility> getFacilitiesBy(Integer programId, List<RequisitionGroup> requisitionGroups) {
        return facilityMapper.getFacilitiesBy(programId, commaSeparator.commaSeparateIds(requisitionGroups));
    }

    public Integer getIdForCode(String code) {
        Integer facilityId = facilityMapper.getIdForCode(code);

        if (facilityId == null)
            throw new DataException("Invalid Facility Code");

        return facilityId;
    }
}
