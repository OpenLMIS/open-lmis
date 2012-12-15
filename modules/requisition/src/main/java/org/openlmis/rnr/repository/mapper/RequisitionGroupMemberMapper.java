package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMemberMapper {

    @Insert("INSERT INTO requisition_group_members" +
            "(requisitionGroupId, facilityId, modifiedBy, modifiedDate) " +
            "VALUES (#{requisitionGroup.id}, #{facility.id}, #{modifiedBy}, #{modifiedDate})")
    Integer insert(RequisitionGroupMember requisitionGroupMember);

    @Select("SELECT rgps.programId " +
            "FROM requisition_groups rg, requisition_group_program_schedules rgps, requisition_group_members rgm " +
            "WHERE rg.id = rgps.requisitionGroupId " +
            "AND rg.id = rgm.requisitionGroupId " +
            "AND rgm.facilityId = #{facilityId}")
    List<Integer> getRequisitionGroupProgramIdsForId(Integer facilityId);

    @Select("SELECT COUNT(*) " +
            "FROM requisition_group_members " +
            "WHERE requisitionGroupId = #{rgId} " +
            "AND facilityId =#{facilityId}")
    Integer doesMappingExist(@Param(value = "rgId") Integer rgId,@Param(value = "facilityId") Integer facilityId);

    @Select("SELECT rg.code " +
            "FROM requisition_groups rg, requisition_group_program_schedules rgps,requisition_group_members rgm " +
            "WHERE RG.id = rgps.requisitionGroupId " +
            "AND rgps.requisitionGroupId = rgm.requisitionGroupId " +
            "AND rgps.programId = #{commonProgramId} " +
            "AND RGM.facilityId = #{facilityId}")
    String getRequisitionGroupCodeForProgramAndFacility(@Param(value = "commonProgramId") Integer commonProgramId,@Param(value = "facilityId") Integer facilityId);
}
