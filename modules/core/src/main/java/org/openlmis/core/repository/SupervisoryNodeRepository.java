package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.getCommaSeparatedRightNames;


@Component
@NoArgsConstructor
public class SupervisoryNodeRepository {
  private SupervisoryNodeMapper supervisoryNodeMapper;
  private FacilityRepository facilityRepository;
  private RequisitionGroupRepository requisitionGroupRepository;

  @Autowired
  public SupervisoryNodeRepository(SupervisoryNodeMapper supervisoryNodeMapper, FacilityRepository facilityRepository, RequisitionGroupRepository requisitionGroupRepository) {
    this.supervisoryNodeMapper = supervisoryNodeMapper;
    this.facilityRepository = facilityRepository;
    this.requisitionGroupRepository = requisitionGroupRepository;
  }

  public void save(SupervisoryNode supervisoryNode) {
    supervisoryNode.getFacility().setId(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode()));
    validateParentNode(supervisoryNode);

    try {
      supervisoryNodeMapper.insert(supervisoryNode);
    } catch (DuplicateKeyException e) {
      throw new DataException("Duplicate SupervisoryNode Code");
    }
  }

  public List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(Integer userId, Integer programId, Right... rights) {
    return supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, getCommaSeparatedRightNames(rights));
  }

  public Integer getIdForCode(String code) {
    Integer supervisoryNodeId = supervisoryNodeMapper.getIdForCode(code);
    if (supervisoryNodeId == null)
      throw new DataException("Invalid SupervisoryNode Code");

    return supervisoryNodeId;
  }

  public Integer getSupervisoryNodeParentId(Integer supervisoryNodeId) {
    SupervisoryNode parent = supervisoryNodeMapper.getSupervisoryNode(supervisoryNodeId).getParent();
    return parent == null ? null : parent.getId();
  }

  private void validateParentNode(SupervisoryNode supervisoryNode) {
    SupervisoryNode parentNode = supervisoryNode.getParent();
    if (parentNode != null) {
      try {
        parentNode.setId(getIdForCode(parentNode.getCode()));
      } catch (DataException e) {
        throw new DataException("Supervisory Node Parent does not exist");
      }
    }
  }

  public SupervisoryNode getFor(Integer facilityId, Integer programId) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(programId, facilityId);
    return (requisitionGroup == null) ? null : supervisoryNodeMapper.getFor(requisitionGroup.getCode());
  }
}
