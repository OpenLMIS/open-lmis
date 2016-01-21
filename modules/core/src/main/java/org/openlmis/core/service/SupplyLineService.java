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

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.SupplyLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling SupplyLine entity.
 */

@Service
public class SupplyLineService {

  @Autowired
  private SupplyLineRepository repository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private SupervisoryNodeRepository supervisoryNodeRepository;

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return repository.getSupplyLineBy(supervisoryNode, program);
  }

  public void save(SupplyLine supplyLine) {
    validateIfSupervisoryNodeIsTopmostNode(supplyLine);

    if (supplyLine.getId() == null) {
      repository.insert(supplyLine);
      return;
    }

    repository.update(supplyLine);
  }

  private void populateIdsForSupplyLine(SupplyLine supplyLine) {
    supplyLine.getProgram().setId(programRepository.getIdByCode(supplyLine.getProgram().getCode()));
    supplyLine.getSupplyingFacility().setId(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode()));
    supplyLine.getSupervisoryNode().setId(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode()));
  }

  private void validateIfSupervisoryNodeIsTopmostNode(SupplyLine supplyLine) {
    Long supervisoryNodeParentId = supervisoryNodeRepository.getSupervisoryNodeParentId(supplyLine.getSupervisoryNode().getId());
    if (supervisoryNodeParentId != null) {
      throw new DataException("error.supervisory.node.not.top.node");
    }
  }

  public SupplyLine getExisting(SupplyLine supplyLine) {
    populateIdsForSupplyLine(supplyLine);
    return repository.getSupplyLineBy(supplyLine.getSupervisoryNode(), supplyLine.getProgram());
  }
  
  public List<Facility> getSupplyingFacilities(Long userId){
    return repository.getSupplyingFacilities(userId);
  }
  
  public SupplyLine getById(Long id) {
    return repository.getById(id);
  }

  public List<SupplyLine> search(String searchParam, String column, Pagination pagination) {
    return repository.search(searchParam, column, pagination);
  }

  public Integer getTotalSearchResultCount(String searchParam, String column) {
    return repository.getTotalSearchResultCount(searchParam, column);
  }


  public SupplyLine getByFacilityProgram(Long facilityId, Long programId){
    return repository.getSupplyLineByFacilityProgram(facilityId, programId);
  }
}