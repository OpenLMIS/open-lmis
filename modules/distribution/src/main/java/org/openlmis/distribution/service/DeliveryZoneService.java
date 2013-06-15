/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.DeliveryZone;
import org.openlmis.distribution.repository.DeliveryZoneRepository;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryZoneService {

  @Autowired
  DeliveryZoneRepository repository;

  @Autowired
  ProgramService programService;

  public void save(DeliveryZone zone) {
    if (zone.getId() != null)
      repository.update(zone);
    else
      repository.insert(zone);
  }

  public DeliveryZone getByCode(String code) {
    return repository.getByCode(code);
  }

  public List<DeliveryZone> getByUserForRight(long userId, Right right) {
    return repository.getByUserForRight(userId, right);
  }

  public List<Program> getProgramsForDeliveryZone(long zoneId) {
    List<Program> programs = repository.getPrograms(zoneId);
    return fillActivePrograms(programs);
  }

  private List<Program> fillActivePrograms(List<Program> programs) {
    List<Program> fullPrograms = new ArrayList<>();
    for (Program program : programs) {
      Program savedProgram = programService.getById(program.getId());
      if (savedProgram.getActive()) fullPrograms.add(savedProgram);
    }
    return fullPrograms;
  }
}
