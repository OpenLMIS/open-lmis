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

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.DeliveryZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the services for handling DeliveryZone entity.
 */

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

  public List<DeliveryZone> getByUserForRight(long userId, String rightName) {
    return repository.getByUserForRight(userId, rightName);
  }

  public List<Program> getActiveProgramsForDeliveryZone(long zoneId) {
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

  public DeliveryZone getById(Long id) {
    return repository.getById(id);
  }

  public List<DeliveryZone> getAll() {
    return repository.getAll();
  }

  public List<Program> getAllProgramsForDeliveryZone(Long zoneId) {
    List<Program> programs = repository.getPrograms(zoneId);
    List<Program> fullPrograms = new ArrayList<>();
    for (Program program : programs) {
      fullPrograms.add(programService.getById(program.getId()));
    }
    return fullPrograms;
  }
}
