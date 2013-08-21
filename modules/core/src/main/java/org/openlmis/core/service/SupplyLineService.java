/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
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

@Service
@NoArgsConstructor
public class SupplyLineService {

  private SupplyLineRepository supplyLineRepository;
  private ProgramRepository programRepository;
  private FacilityRepository facilityRepository;
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  public SupplyLineService(SupplyLineRepository supplyLineRepository, ProgramRepository programRepository, FacilityRepository facilityRepository, SupervisoryNodeRepository supervisoryNodeRepository) {
    this.supplyLineRepository = supplyLineRepository;
    this.programRepository = programRepository;
    this.facilityRepository = facilityRepository;
    this.supervisoryNodeRepository = supervisoryNodeRepository;
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return supplyLineRepository.getSupplyLineBy(supervisoryNode, program);
  }

  public void save(SupplyLine supplyLine) {
    validateIfSupervisoryNodeIsTopmostNode(supplyLine);

    if (supplyLine.getId() == null) {
      this.supplyLineRepository.insert(supplyLine);
      return;
    }

    this.supplyLineRepository.update(supplyLine);
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
    return supplyLineRepository.getSupplyLineBy(supplyLine.getSupervisoryNode(), supplyLine.getProgram());
  }
}
