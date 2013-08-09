/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.event.ProgramChangeEvent;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

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

  public List<Program> getProgramsSupportedByUserHomeFacilityWithRights(Long facilityId, Long userId, Right... rights) {
    return programRepository.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, rights);
  }

  public List<Program> getProgramForSupervisedFacilities(Long userId, Right... rights) {
    return programRepository.getUserSupervisedActiveProgramsWithRights(userId, rights);
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

  public List<Program> getProgramsForUserByFacilityAndRights(Long facilityId, Long userId, Right... rights) {
    return programRepository.getProgramsForUserByFacilityAndRights(facilityId, userId, rights);
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
}