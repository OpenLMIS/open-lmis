package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.mapper.RequisitionGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class RequisitionGroupRepository {

  private RequisitionGroupMapper requisitionGroupMapper;
  private FacilityMapper facilityMapper;

  @Autowired
  public RequisitionGroupRepository(RequisitionGroupMapper requisitionGroupMapper, FacilityMapper facilityMapper) {
    this.requisitionGroupMapper = requisitionGroupMapper;
    this.facilityMapper = facilityMapper;
  }

  public void save(RequisitionGroup requisitionGroup) {
    try {
      if (facilityMapper.getIdForCode(requisitionGroup.getHeadFacilityCode()) == null) {
        throw new RuntimeException("Head Facility Not Found");
      }
      requisitionGroupMapper.save(requisitionGroup);
    } catch (DuplicateKeyException e) {
      throw new RuntimeException("Duplicate Requisition Group Code found");
    } catch (DataIntegrityViolationException e) {
      throw new RuntimeException("Parent RG code not found");
    }
  }
}
