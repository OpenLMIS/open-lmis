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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * ProgramSupportedRepository is Repository class for FacilitySupportedProgram related database operations.
 */

@NoArgsConstructor
@Repository
public class ProgramSupportedRepository {

  @Autowired
  private ProgramSupportedMapper mapper;

  public Date getProgramStartDate(Long facilityId, Long programId) {
    return mapper.getBy(facilityId, programId).getStartDate();
  }

  public void deleteSupportedPrograms(Long facilityId, Long programId) {
    mapper.delete(facilityId, programId);
  }

  public void addSupportedProgram(ProgramSupported programSupported) {
    try {
      mapper.insert(programSupported);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.facility.program.mapping.exists");
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("error.reference.data.invalid.program");
    }
  }

  //TODO simplify
  public boolean updateSupportedPrograms(Facility facility) {
    boolean change = false;
    List<ProgramSupported> previouslySupportedPrograms = mapper.getAllByFacilityId(facility.getId());
    Iterator<ProgramSupported> previousPSIterator = previouslySupportedPrograms.iterator();
    while (previousPSIterator.hasNext()) {
      ProgramSupported previousProgramSupported = previousPSIterator.next();
      Iterator<ProgramSupported> newPSIterator = facility.getSupportedPrograms().iterator();
      while (newPSIterator.hasNext()) {
        ProgramSupported newProgramSupported = newPSIterator.next();
        Long newProgramSupportedId = newProgramSupported.getProgram().getId();
        Long previousProgramSupportedId = previousProgramSupported.getProgram().getId();

        if (previousProgramSupportedId.equals(newProgramSupportedId)) {
          if (changeInProgramSupported(previousProgramSupported, newProgramSupported)) {
            newProgramSupported.setFacilityId(facility.getId());
            newProgramSupported.setModifiedBy(facility.getModifiedBy());
            mapper.update(newProgramSupported);
            change = true;
          }
          newPSIterator.remove();
          previousPSIterator.remove();
          break;
        }
      }
    }

    for (ProgramSupported ps : facility.getSupportedPrograms()) {
      ps.setFacilityId(facility.getId());
      ps.setModifiedBy(facility.getModifiedBy());
      ps.setCreatedBy(facility.getModifiedBy());
      mapper.insert(ps);
      change = true;
    }
    for (ProgramSupported ps : previouslySupportedPrograms) {
      mapper.delete(facility.getId(), ps.getProgram().getId());
      change = true;
    }
    return change;
  }

  public List<ProgramSupported> getAllByFacilityId(Long facilityId) {
    return mapper.getAllByFacilityId(facilityId);
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return mapper.getBy(facilityId, programId);
  }

  public void updateSupportedProgram(ProgramSupported programSupported) {
    mapper.update(programSupported);
  }

  public List<ProgramSupported> getActiveByFacilityId(Long facilityId) {
    return mapper.getActiveProgramsByFacilityId(facilityId);
  }

  public void updateForVirtualFacilities(Facility parentFacility) {
    mapper.deleteVirtualFacilityProgramSupported(parentFacility);
    mapper.copyToVirtualFacilities(parentFacility);
  }

  private boolean changeInProgramSupported(ProgramSupported ps1, ProgramSupported ps2) {
    if (ps1.getActive() != ps2.getActive()) {
      return true;
    }
    if (ps1.getStartDate() == null) {
      return ps2.getStartDate() != null;
    }
    return !(ps1.getStartDate().equals(ps2.getStartDate()));
  }

}
