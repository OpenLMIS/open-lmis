package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class SupervisoryNodeService {
    private SupervisoryNodeRepository supervisoryNodeRepository;

    @Autowired
    public SupervisoryNodeService(SupervisoryNodeRepository supervisoryNodeRepository) {
        this.supervisoryNodeRepository = supervisoryNodeRepository;
    }

    public void save(SupervisoryNode supervisoryNode) {
        supervisoryNodeRepository.save(supervisoryNode);
    }

    public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Integer programId, Right right) {
        return supervisoryNodeRepository.getAllSupervisoryNodesInHierarchyBy(userId, programId, right);
    }

  public SupervisoryNode getFor(int facilityId, int programId) {
    return supervisoryNodeRepository.getFor(facilityId, programId);
  }
}
