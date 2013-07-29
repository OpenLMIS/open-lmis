/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

@NoArgsConstructor
@Repository
public class ProgramSupportedRepository {

  private ProgramSupportedMapper programSupportedMapper;

  @Autowired
  public ProgramSupportedRepository(ProgramSupportedMapper programSupportedMapper) {
    this.programSupportedMapper = programSupportedMapper;
  }

  public Date getProgramStartDate(Long facilityId, Long programId) {
    return programSupportedMapper.getBy(facilityId, programId).getStartDate();
  }

  public void deleteSupportedPrograms(Long facilityId, Long programId) {
    programSupportedMapper.delete(facilityId, programId);
  }

  public void addSupportedProgram(ProgramSupported programSupported) {
    try {
      programSupportedMapper.insert(programSupported);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.facility.program.mapping.exists");
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("error.reference.data.invalid.program");
    }
  }

  //TODO simplify
  public void updateSupportedPrograms(Facility facility) {
    List<ProgramSupported> previouslySupportedPrograms = programSupportedMapper.getAllByFacilityId(facility.getId());
    Iterator<ProgramSupported> previousPSIterator = previouslySupportedPrograms.iterator();
    while (previousPSIterator.hasNext()) {
      ProgramSupported previousProgramSupported = previousPSIterator.next();
      Iterator<ProgramSupported> newPSIterator = facility.getSupportedPrograms().iterator();
      while (newPSIterator.hasNext()) {
        ProgramSupported newProgramSupported = newPSIterator.next();
        if (previousProgramSupported.getProgram().getId().equals(newProgramSupported.getProgram().getId())) {
          newProgramSupported.setFacilityId(facility.getId());
          newProgramSupported.setModifiedBy(facility.getModifiedBy());
          programSupportedMapper.update(newProgramSupported);
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
      programSupportedMapper.insert(ps);
    }
    for (ProgramSupported ps : previouslySupportedPrograms) {
      programSupportedMapper.delete(facility.getId(), ps.getProgram().getId());
    }

  }

  public List<ProgramSupported> getAllByFacilityId(Long facilityId) {
    return programSupportedMapper.getAllByFacilityId(facilityId);
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return programSupportedMapper.getBy(facilityId, programId);
  }

  public void updateSupportedProgram(ProgramSupported programSupported) {
    programSupportedMapper.update(programSupported);
  }
}
