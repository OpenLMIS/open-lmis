package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class RequisitionGroupRepository {

    private RequisitionGroupMapper requisitionGroupMapper;
    private SupervisoryNodeMapper supervisoryNodeMapper;


    @Autowired
    public RequisitionGroupRepository(RequisitionGroupMapper requisitionGroupMapper, SupervisoryNodeMapper supervisoryNodeMapper) {
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.supervisoryNodeMapper = supervisoryNodeMapper;
    }

    public void insert(RequisitionGroup requisitionGroup) {
        try {
            requisitionGroup.getSupervisoryNode().setId(supervisoryNodeMapper.getIdForCode(requisitionGroup.getSupervisoryNode().getCode()));
            if (requisitionGroup.getSupervisoryNode().getId() == null) {
                throw new RuntimeException("Supervisory Node Not Found");
            }
            requisitionGroupMapper.insert(requisitionGroup);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Duplicate Requisition Group Code found");
        }
    }
}
