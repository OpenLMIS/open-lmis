package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
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


  public RequisitionGroup getRequisitionGroupForProgramAndFacility(Integer programId, Integer facilityId) {
    return mapper.getRequisitionGroupForProgramAndFacility(programId, facilityId);
  }
}
