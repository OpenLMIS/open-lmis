/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RequisitionGroupRepository {

  private RequisitionGroupMapper mapper;
  private CommaSeparator commaSeparator;


  @Autowired
  public RequisitionGroupRepository(RequisitionGroupMapper requisitionGroupMapper, CommaSeparator commaSeparator) {
    this.mapper = requisitionGroupMapper;
    this.commaSeparator = commaSeparator;
  }

  public void insert(RequisitionGroup requisitionGroup) {
    try {
      mapper.insert(requisitionGroup);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate Requisition Group Code found");
    }
  }

  public List<RequisitionGroup> getRequisitionGroups(List<SupervisoryNode> supervisoryNodes) {
    return mapper.getRequisitionGroupBySupervisoryNodes(commaSeparator.commaSeparateIds(supervisoryNodes));
  }


  public RequisitionGroup getRequisitionGroupForProgramAndFacility(Program program, Facility facility) {
    return mapper.getRequisitionGroupForProgramAndFacility(program, facility);
  }
}
