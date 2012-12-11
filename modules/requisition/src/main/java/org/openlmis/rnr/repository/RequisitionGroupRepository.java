package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class RequisitionGroupRepository {

    private RequisitionGroupMapper requisitionGroupMapper;
    private FacilityMapper facilityMapper;
    private RequisitionGroupMemberMapper requisitionGroupMemberMapper;

    @Autowired
    public RequisitionGroupRepository(RequisitionGroupMapper requisitionGroupMapper, FacilityMapper facilityMapper, RequisitionGroupMemberMapper requisitionGroupMemberMapper) {
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.facilityMapper = facilityMapper;
        this.requisitionGroupMemberMapper = requisitionGroupMemberMapper;
    }

    public void insert(RequisitionGroup requisitionGroup) {
        Long headFacilityId;

        try {
            headFacilityId = facilityMapper.getIdForCode(requisitionGroup.getHeadFacility().getCode());
            if (headFacilityId == null) {
                throw new RuntimeException("Head Facility Not Found");
            }
            requisitionGroup.getHeadFacility().setId(headFacilityId);
            requisitionGroup.setId(requisitionGroupMapper.insert(requisitionGroup));
            RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember(requisitionGroup.getId(), requisitionGroup.getHeadFacility().getId());
            requisitionGroupMember.setModifiedBy(requisitionGroup.getModifiedBy());
            requisitionGroupMemberMapper.insert(requisitionGroupMember);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Duplicate Requisition Group Code found");
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Parent RG code not found");
        }
    }
}
