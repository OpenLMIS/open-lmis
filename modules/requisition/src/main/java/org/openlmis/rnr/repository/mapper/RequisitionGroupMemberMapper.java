package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMemberMapper {

    @Insert("INSERT INTO requisition_group_members(requisition_group_id, facility_id, modified_by, modified_date) VALUES (#{requisitionGroup.id}, #{facility.id}, #{modifiedBy}, #{modifiedDate})")
    Integer insert(RequisitionGroupMember requisitionGroupMember);

    @Select("SELECT rgps.program_id FROM requisition_group AS rg, requisition_group_program_schedule AS rgps, requisition_group_members AS rgm " +
            "WHERE rg.id = rgps.requisition_group_id AND rg.id = rgm.requisition_group_id AND rgm.facility_id = #{facilityId}")
    List<Integer> getRequisitionGroupProgramIdsForId(Integer facilityId);



    @Select("SELECT COUNT(*) FROM requisition_group_members WHERE requisition_group_id = #{rgId} AND facility_id =#{facilityId}")
    Integer doesMappingExist(@Param(value = "rgId") Integer rgId,@Param(value = "facilityId") Integer facilityId);


    @Select("SELECT RG.code FROM requisition_group RG, requisition_group_program_schedule RGPS,requisition_group_members RGM " +
            "WHERE RG.id = RGPS.requisition_group_id AND RGPS.requisition_group_id = RGM.requisition_group_id AND RGPS.program_id = #{commonProgramId} AND " +
            "RGM.facility_id = #{facilityId}")
    String getRequisitionGroupCodeForProgramAndFacility(@Param(value = "commonProgramId") Integer commonProgramId,@Param(value = "facilityId") Integer facilityId);
}
