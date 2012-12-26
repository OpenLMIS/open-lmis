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

    private RequisitionGroupMapper requisitionGroupMapper;
    private SupervisoryNodeRepository supervisoryNodeRepository;
    private CommaSeparator commaSeparator;


    @Autowired
    public RequisitionGroupRepository(RequisitionGroupMapper requisitionGroupMapper, SupervisoryNodeRepository supervisoryNodeRepository, CommaSeparator commaSeparator) {
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.supervisoryNodeRepository = supervisoryNodeRepository;
        this.commaSeparator = commaSeparator;
    }

    public void insert(RequisitionGroup requisitionGroup) {
        requisitionGroup.getSupervisoryNode().setId(supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode()));

        try {
            requisitionGroupMapper.insert(requisitionGroup);
        } catch (DuplicateKeyException e) {
            throw new DataException("Duplicate Requisition Group Code found");
        }
    }

    public List<RequisitionGroup> getRequisitionGroups(List<SupervisoryNode> supervisoryNodes) {

        return requisitionGroupMapper.getRequisitionGroupBySupervisoryNodes(commaSeparator.commaSeparateIds(supervisoryNodes));
    }


}
