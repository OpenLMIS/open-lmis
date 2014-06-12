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
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RequisitionGroupRepository is Repository class for RequisitionGroup related database operations.
 */

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
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.duplicate.code.requisition.group");
    }
  }

  public List<RequisitionGroup> getRequisitionGroups(List<SupervisoryNode> supervisoryNodes) {
    return mapper.getRequisitionGroupBySupervisoryNodes(commaSeparator.commaSeparateIds(supervisoryNodes));
  }

  public RequisitionGroup getRequisitionGroupForProgramAndFacility(Program program, Facility facility) {
    return mapper.getRequisitionGroupForProgramAndFacility(program, facility);
  }

  public RequisitionGroup getByCode(RequisitionGroup requisitionGroup) {
    return mapper.getByCode(requisitionGroup.getCode());
  }

  public void update(RequisitionGroup requisitionGroup) {
    try {
      mapper.update(requisitionGroup);
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.duplicate.code.requisition.group");
    }
  }

  public List<RequisitionGroup> searchByGroupName(String searchParam, Pagination pagination) {
    return mapper.searchByGroupName(searchParam, pagination);
  }

  public List<RequisitionGroup> searchByNodeName(String searchParam, Pagination pagination) {
    return mapper.searchByNodeName(searchParam, pagination);
  }

  public Integer getTotalRecordsForSearchOnGroupName(String searchParam) {
    return mapper.getTotalRecordsForSearchOnGroupName(searchParam);
  }

  public Integer getTotalRecordsForSearchOnNodeName(String searchParam) {
    return mapper.getTotalRecordsForSearchOnNodeName(searchParam);
  }

  public RequisitionGroup getBy(Long id) {
    return mapper.getRequisitionGroupById(id);
  }

}
