/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class SupplyLineRepository {

  private SupplyLineMapper supplyLineMapper;
  private ProgramRepository programRepository;
  private FacilityRepository facilityRepository;
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Autowired
  public SupplyLineRepository(SupplyLineMapper supplyLineMapper, SupervisoryNodeRepository supervisoryNodeRepository, ProgramRepository programRepository, FacilityRepository facilityRepository) {
    this.supplyLineMapper = supplyLineMapper;
    this.supervisoryNodeRepository = supervisoryNodeRepository;
    this.programRepository = programRepository;
    this.facilityRepository = facilityRepository;
  }

  public void insert(SupplyLine supplyLine) {
    supplyLine.getProgram().setId(programRepository.getIdByCode(supplyLine.getProgram().getCode()));
    supplyLine.getSupplyingFacility().setId(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode()));
    supplyLine.getSupervisoryNode().setId(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode()));

    validateIfSupervisoryNodeIsTopmostNode(supplyLine);

    try {
      supplyLineMapper.insert(supplyLine);
    } catch (DuplicateKeyException ex) {
      throw new DataException("Duplicate entry for Supply Line found");
    }
  }

  private void validateIfSupervisoryNodeIsTopmostNode(SupplyLine supplyLine) {
    Integer supervisoryNodeParentId = supervisoryNodeRepository.getSupervisoryNodeParentId(supplyLine.getSupervisoryNode().getId());
    if (supervisoryNodeParentId != null)
      throw new DataException("Supervising Node is not the Top node");
  }


  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return supplyLineMapper.getSupplyLineBy(supervisoryNode, program);
  }
}
