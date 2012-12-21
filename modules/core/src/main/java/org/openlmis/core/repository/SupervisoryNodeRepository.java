package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@NoArgsConstructor
public class SupervisoryNodeRepository {
    private SupervisoryNodeMapper supervisoryNodeMapper;
    private FacilityMapper facilityMapper;

    @Autowired
    public SupervisoryNodeRepository(SupervisoryNodeMapper supervisoryNodeMapper, FacilityMapper facilityMapper) {
        this.supervisoryNodeMapper = supervisoryNodeMapper;
        this.facilityMapper = facilityMapper;
    }

    public void save(SupervisoryNode supervisoryNode) {
        supervisoryNode.getFacility().setId(facilityMapper.getIdForCode(supervisoryNode.getFacility().getCode()));
        if (supervisoryNode.getParent() != null) {
            supervisoryNode.getParent().setId(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode()));
            if (supervisoryNode.getParent().getId() == null) {
                throw new DataException("Supervisory Node as Parent does not exist");
            }
        }
        if (supervisoryNode.getFacility().getId() == null) {
            throw new DataException("Facility Code does not exist");
        }

        try {
            supervisoryNodeMapper.insert(supervisoryNode);
        } catch (DuplicateKeyException e) {
            throw new DataException("Duplicate Supervisory Node Code");
        }
    }

    public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Integer programId, Right right) {
        return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, right);
    }
}
