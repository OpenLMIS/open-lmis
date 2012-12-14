package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMemberMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;

@Repository
@NoArgsConstructor
public class RequisitionGroupMemberRepository {

    private RequisitionGroupMemberMapper requisitionGroupMemberMapper;
    private RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;
    private RequisitionGroupMapper requisitionGroupMapper;
    private FacilityMapper facilityMapper;
    private ProgramMapper programMapper;

    @Autowired
    public RequisitionGroupMemberRepository(RequisitionGroupMemberMapper requisitionGroupMemberMapper, RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
                                            RequisitionGroupMapper requisitionGroupMapper, FacilityMapper facilityMapper, ProgramMapper programMapper) {
        this.requisitionGroupMemberMapper = requisitionGroupMemberMapper;
        this.requisitionGroupProgramScheduleMapper = requisitionGroupProgramScheduleMapper;
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.facilityMapper = facilityMapper;
        this.programMapper = programMapper;
    }


    public void insert(RequisitionGroupMember requisitionGroupMember) {
        requisitionGroupMember.getFacility().setId(facilityMapper.getIdForCode(requisitionGroupMember.getFacility().getCode()));

        requisitionGroupMember.getRequisitionGroup().setId(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode()));

        List<Integer> programIDsForRG = requisitionGroupProgramScheduleMapper.getProgramIDsbyId(requisitionGroupMember.getRequisitionGroup().getId());
        List<Integer> requisitionGroupProgramIdsForFacility = requisitionGroupMemberMapper.getRequisitionGroupProgramIdsForId(requisitionGroupMember.getFacility().getId());

        if (requisitionGroupMember.getRequisitionGroup().getId() == null) {
            throw new RuntimeException("Requisition Group does not exist");
        }
        if (requisitionGroupMember.getFacility().getId() == null) {
            throw new RuntimeException("Facility does not exist");
        }

        if (requisitionGroupMemberMapper.doesMappingExist(requisitionGroupMember.getRequisitionGroup().getId(), requisitionGroupMember.getFacility().getId()) == 1) {
            throw new RuntimeException("Facility to Requisition Group mapping already exists");

        }

        if (programIDsForRG.size() == 0) {
            throw new RuntimeException("No Program(s) mapped for Requisition Group");
        }

        List<Integer> commonProgramsId = intersection(requisitionGroupProgramIdsForFacility, programIDsForRG);
        if (commonProgramsId.size() > 0) {
            Program duplicateProgram = programMapper.getById(commonProgramsId.get(0));
            duplicateProgram.setId(commonProgramsId.get(0));
            throw new RuntimeException("Facility " + requisitionGroupMember.getFacility().getCode() + " is already assigned to Requisition Group " +
                    requisitionGroupMemberMapper.getRequisitionGroupCodeForProgramAndFacility(duplicateProgram.getId(), requisitionGroupMember.getFacility().getId())
                    + " running same program " + duplicateProgram.getCode());
        }

        requisitionGroupMemberMapper.insert(requisitionGroupMember);
    }

}
