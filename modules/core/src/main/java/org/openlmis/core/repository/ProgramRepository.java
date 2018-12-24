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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.commaSeparateRightNames;

/**
 * ProgramRepository is Repository class for Program related database operations.
 */

@Component
@NoArgsConstructor
public class ProgramRepository {

  private ProgramMapper mapper;

  private ProgramSupportedMapper programSupportedMapper;

  private RegimenMapper regimenMapper;

  public static String PROGRAM_CODE_INVALID = "program.code.invalid";

  @Autowired
  public ProgramRepository(ProgramMapper programMapper, ProgramSupportedMapper programSupportedMapper,
                           RegimenMapper regimenMapper) {
    this.mapper = programMapper;
    this.programSupportedMapper = programSupportedMapper;
    this.regimenMapper = regimenMapper;
  }

  public List<Program> getByFacility(Long facilityId) {
    return mapper.getByFacilityId(facilityId);
  }

  public List<Program> getAllPullPrograms() {
    return mapper.getAllPullPrograms();
  }

  public List<Program> getAllPushPrograms() {
    return mapper.getAllPushPrograms();
  }


  public List<Program> getUserSupervisedActiveProgramsWithRights(Long userId, String... rightNames) {
    return mapper.getUserSupervisedActivePrograms(userId, commaSeparateRightNames(rightNames));
  }

  public List<Program> getUserSupervisedActiveIvdProgramsWithRights(Long userId, String... rightNames) {
    return mapper.getUserSupervisedActiveIvdPrograms(userId, commaSeparateRightNames(rightNames));
  }

  public List<Program> getProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, String... rightNames) {
    return mapper.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, commaSeparateRightNames(rightNames));
  }

  public List<Program> getIvdProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, String... rightNames) {
    return mapper.getIvdProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, commaSeparateRightNames(rightNames));
  }


  public Long getIdByCode(String code) {
    Long programId = mapper.getIdForCode(code);

    if (programId == null) {
      throw new DataException(new OpenLmisMessage(PROGRAM_CODE_INVALID));
    }

    return programId;
  }

  public List<Program> getActiveProgramsForUserWithRights(Long userId, String... rightNames) {
    return mapper.getActiveProgramsForUserWithRights(userId, commaSeparateRightNames(rightNames));
  }

  public Program getById(Long id) {
    return mapper.getById(id);
  }

  public void setTemplateConfigured(Long id) {
    mapper.setTemplateConfigured(id);
  }

  public List<Program> getProgramsForUserByFacilityAndRights(Long facilityId, Long userId, String... rightNames) {
    return mapper.getProgramsForUserByFacilityAndRights(facilityId, userId, commaSeparateRightNames(rightNames));
  }

  public List<Program> getAll() {
    return mapper.getAll();
  }

  public Program getByCode(String code) {
    return mapper.getByCode(code);
  }

  public void setRegimenTemplateConfigured(Long programId) {
    mapper.setRegimenTemplateConfigured(programId);
  }

  public void setFeedSendFlag(Program program, Boolean sendFeed) {
    mapper.setFeedSendFlag(program, sendFeed);
  }

  public List<Program> getProgramsForNotification() {
    return mapper.getProgramsForNotification();
  }

  public Program update(Program program) {

      if(program.getBudgetingApplies() == false)
          program.setUsePriceSchedule(false);

    mapper.update(program);
    return mapper.getById(program.getId());
  }

  public List<Program> getAllIvdPrograms() {
    return mapper.getAllIvdPrograms();

  }

  public void associateParent(Long parentProgramId, String programCode) {
    Long programId = getIdByCode(programCode);
    mapper.associateProgramToParent(programId, parentProgramId);
  }

  public Program getProgramWithParentById(Long programId) {
    return mapper.getWithParentById(programId);
  }

  @Transactional
  public void toPersistDbByOperationType(List<Program> saves, List<Program> updates, Map<Long, Boolean> toActives) {

    for (Program save : saves) {
      mapper.insert(save);
    }

    for (Program update : updates) {
      mapper.updateBasicInfo(update);
    }

    for (Map.Entry<Long, Boolean> entry : toActives.entrySet()) {
      programSupportedMapper.updateActiveByProgramId(entry.getKey(), entry.getValue());
      regimenMapper.updateActiveByProgramId(entry.getKey(), entry.getValue());
    }

  }
}
