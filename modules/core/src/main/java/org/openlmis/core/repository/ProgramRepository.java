/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Component
@NoArgsConstructor
public class ProgramRepository {

  private ProgramMapper mapper;

  public static String PROGRAM_CODE_INVALID = "program.code.invalid";

  @Autowired
  public ProgramRepository(ProgramMapper programMapper) {
    this.mapper = programMapper;
  }

  public List<Program> getByFacility(Long facilityId) {
    return mapper.getByFacilityId(facilityId);
  }

  public List<Program> getAllPullPrograms() {
    return mapper.getAllPullPrograms();
  }

  public List<Program> getUserSupervisedActiveProgramsWithRights(Long userId, Right... rights) {
    return mapper.getUserSupervisedActivePrograms(userId, commaSeparateRightNames(rights));
  }

  public List<Program> getProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, Right... rights) {
    return mapper.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, commaSeparateRightNames(rights));
  }

  public Long getIdByCode(String code) {
    Long programId = mapper.getIdForCode(code);

    if (programId == null) {
      throw new DataException(new OpenLmisMessage(PROGRAM_CODE_INVALID));
    }

    return programId;
  }

  public List<Program> getActiveProgramsForUserWithRights(Long userId, Right... rights) {
    return mapper.getActiveProgramsForUserWithRights(userId, commaSeparateRightNames(rights));
  }

  public Program getById(Long id) {
    return mapper.getById(id);
  }

  public void setTemplateConfigured(Long id) {
    mapper.setTemplateConfigured(id);
  }

  public List<Program> getProgramsForUserByFacilityAndRights(Long facilityId, Long userId, Right... rights) {
    return mapper.getProgramsForUserByFacilityAndRights(facilityId, userId, commaSeparateRightNames(rights));
  }

  public List<Program> getAll() {
    return mapper.getAll();
  }
}
