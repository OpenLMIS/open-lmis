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
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
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
@NoArgsConstructor
public class SupplyLineService {

  private SupplyLineRepository repository;
  private ProgramRepository programRepository;
  private FacilityRepository facilityRepository;
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  public SupplyLineService(SupplyLineRepository repository, ProgramRepository programRepository, FacilityRepository facilityRepository, SupervisoryNodeRepository supervisoryNodeRepository) {
    this.repository = repository;
    this.programRepository = programRepository;
    this.facilityRepository = facilityRepository;
    this.supervisoryNodeRepository = supervisoryNodeRepository;
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return repository.getSupplyLineBy(supervisoryNode, program);
  }

  public void save(SupplyLine supplyLine) {
    validateIfSupervisoryNodeIsTopmostNode(supplyLine);

    if (supplyLine.getId() == null) {
      this.repository.insert(supplyLine);
      return;
    }

    this.repository.update(supplyLine);
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

  public SupplyLine getById(Long id) {
    return repository.getById(id);
  }

  public List<SupplyLine> search(String searchParam, String columnName, Pagination pagination) {
    return repository.search(searchParam, columnName, pagination);
  }

  public Integer getTotalSearchResultCount(String searchParam, String columnName) {
    return repository.getTotalSearchResultCount(searchParam, columnName);
  }
}
