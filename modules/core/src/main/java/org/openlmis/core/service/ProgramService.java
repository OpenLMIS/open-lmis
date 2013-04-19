/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class ProgramService {
  private ProgramRepository programRepository;
  private ProgramSupportedRepository programSupportedRepository;

  @Autowired
  public ProgramService(ProgramRepository programRepository, ProgramSupportedRepository programSupportedRepository) {
    this.programRepository = programRepository;
    this.programSupportedRepository = programSupportedRepository;
  }

  public List<Program> getByFacility(Integer facilityId) {
    return programRepository.getByFacility(facilityId);
  }

  public List<Program> getAll() {
    return programRepository.getAll();
  }

  public List<Program> getProgramsSupportedByFacilityForUserWithRights(Integer facilityId, Integer userId, Right... rights) {
    return programRepository.getProgramsSupportedByFacilityForUserWithRights(facilityId, userId, rights);
  }

  public List<Program> getUserSupervisedActiveProgramsWithRights(Integer userId, Right... rights) {
    return programRepository.getUserSupervisedActiveProgramsWithRights(userId, rights);
  }

  public Integer getIdForCode(String code) {
    return programRepository.getIdByCode(code);
  }

  public Date getProgramStartDate(Integer facilityId, Integer programId) {
    return programSupportedRepository.getProgramStartDate(facilityId, programId);
  }

  public Program getById(Integer id) {
    return programRepository.getById(id);
  }

  public void setTemplateConfigured(int id) {
    programRepository.setTemplateConfigured(id);
  }
}