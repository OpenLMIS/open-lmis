/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.Program;
import org.openlmis.core.event.ProgramChangeEvent;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Exposes the services for handling Program entity.
 */

@Service
@NoArgsConstructor
public class ProgramService {

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProgramSupportedRepository programSupportedRepository;

  @Autowired
  private EventService eventService;

  public List<Program> getByFacility(Long facilityId) {
    return programRepository.getByFacility(facilityId);
  }

  public List<Program> getAllPullPrograms() {
    return programRepository.getAllPullPrograms();
  }

  public List<Program> getAllPushPrograms() {
    return programRepository.getAllPushPrograms();
  }

  public List<Program> getAllIvdPrograms(){
    return programRepository.getAllIvdPrograms();
  }

  public List<Program> getProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, String... rightNames) {
    return programRepository.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, rightNames);
  }

  public List<Program> getIvdProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, String... rightNames) {
    return programRepository.getIvdProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, rightNames);
  }

  public List<Program> getProgramForSupervisedFacilities(Long userId, String... rightNames) {
    return programRepository.getUserSupervisedActiveProgramsWithRights(userId, rightNames);
  }


  public List<Program> getIvdProgramForSupervisedFacilities(Long userId, String... rightNames) {
    return programRepository.getUserSupervisedActiveIvdProgramsWithRights(userId, rightNames);
  }

  public Long getIdForCode(String code) {
    return programRepository.getIdByCode(code);
  }

  public Date getProgramStartDate(Long facilityId, Long programId) {
    return programSupportedRepository.getProgramStartDate(facilityId, programId);
  }

  public Program getById(Long id) {
    return programRepository.getById(id);
  }

  public void setTemplateConfigured(Long id) {
    programRepository.setTemplateConfigured(id);
  }

  public List<Program> getProgramsForUserByFacilityAndRights(Long facilityId, Long userId, String... rightNames) {
    return programRepository.getProgramsForUserByFacilityAndRights(facilityId, userId, rightNames);
  }

  public List<Program> getAll() {
    return programRepository.getAll();
  }

  public Program getByCode(String code) {
    return programRepository.getByCode(code);
  }

  public void setRegimenTemplateConfigured(Long programId) {
    programRepository.setRegimenTemplateConfigured(programId);
  }

  public void setFeedSendFlag(Program program, Boolean sendFeed) {
    programRepository.setFeedSendFlag(program, sendFeed);
  }

  public void notifyProgramChange() {
    List<Program> programsForNotifications = programRepository.getProgramsForNotification();
    for (Program program : programsForNotifications) {
      try {
        eventService.notify(new ProgramChangeEvent(program));
        programRepository.setFeedSendFlag(program, false);
      } catch (URISyntaxException e) {
        throw new DataException("error.malformed.uri");
      }
    }
  }

  public Program getValidatedProgramByCode(String programCode) {
    Program program = programRepository.getByCode(programCode);
    if (program == null) {
      throw new DataException("program.code.invalid");
    }
    return program;
  }

  public Program update(Program program) {
    return programRepository.update(program);
  }

  public List<Program> getProgramsForUserByRights(Long userId, String rightName) {
    return programRepository.getActiveProgramsForUserWithRights(userId, rightName);
  }
}