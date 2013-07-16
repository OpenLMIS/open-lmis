/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.subtract;

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
      programSupportedMapper.addSupportedProgram(programSupported);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.facility.program.mapping.exists");
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("error.reference.data.invalid.program");
    }
  }

  public void addSupportedProgramsFor(Facility facility) {
    for (ProgramSupported supportedProgram : facility.getSupportedPrograms()) {
      supportedProgram.setModifiedBy(facility.getModifiedBy());
      supportedProgram.setFacilityId(facility.getId());
      addSupportedProgram(supportedProgram);
    }
  }

  public void updateSupportedPrograms(Facility facility) {
    List<ProgramSupported> previouslySupportedPrograms = programSupportedMapper.getAllByFacilityId(facility.getId());
    deleteObsoleteProgramMappings(facility, previouslySupportedPrograms);
    addUpdatableProgramMappings(facility, previouslySupportedPrograms);
  }

  private void deleteObsoleteProgramMappings(Facility facility, List<ProgramSupported> previouslySupportedPrograms) {
    List<ProgramSupported> supportedPrograms = facility.getSupportedPrograms();
    for (ProgramSupported programSupported : (Collection<ProgramSupported>) subtract(previouslySupportedPrograms, supportedPrograms)) {
      deleteSupportedPrograms(facility.getId(), programSupported.getProgram().getId());
    }
  }

  private void addUpdatableProgramMappings(Facility facility, List<ProgramSupported> previouslySupportedPrograms) {
    List<ProgramSupported> supportedPrograms = facility.getSupportedPrograms();
    for (ProgramSupported programSupported : (Collection<ProgramSupported>) subtract(supportedPrograms, previouslySupportedPrograms)) {
      programSupported.setFacilityId(facility.getId());
      programSupported.setModifiedBy(facility.getModifiedBy());
      addSupportedProgram(programSupported);
    }
  }

  public List<ProgramSupported> getAllByFacilityId(Long facilityId) {
    return programSupportedMapper.getAllByFacilityId(facilityId);
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return programSupportedMapper.getBy(facilityId, programId);
  }

  public void updateSupportedProgram(ProgramSupported programSupported) {
    programSupportedMapper.updateSupportedProgram(programSupported);
  }
}
