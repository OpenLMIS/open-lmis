package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.core.domain.RequisitionGroupMember;
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
    private FacilityRepository facilityRepository;
    private ProgramMapper programMapper;

    @Autowired
    public RequisitionGroupMemberRepository(RequisitionGroupMemberMapper requisitionGroupMemberMapper, RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper,
                                            RequisitionGroupMapper requisitionGroupMapper, FacilityRepository facilityRepository, ProgramMapper programMapper) {
        this.requisitionGroupMemberMapper = requisitionGroupMemberMapper;
        this.requisitionGroupProgramScheduleMapper = requisitionGroupProgramScheduleMapper;
        this.requisitionGroupMapper = requisitionGroupMapper;
        this.facilityRepository = facilityRepository;
        this.programMapper = programMapper;
    }


    public void insert(RequisitionGroupMember requisitionGroupMember) {
        requisitionGroupMember.getFacility().setId(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode()));
        requisitionGroupMember.getRequisitionGroup().setId(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode()));

        List<Integer> requisitionGroupProgramIdsForFacility = requisitionGroupMemberMapper.getRequisitionGroupProgramIdsForId(requisitionGroupMember.getFacility().getId());

        if (requisitionGroupMember.getRequisitionGroup().getId() == null) {
            throw new DataException("Requisition Group does not exist");
        }

        // TODO : can be done through db constraints
        if (requisitionGroupMemberMapper.doesMappingExist(requisitionGroupMember.getRequisitionGroup().getId(), requisitionGroupMember.getFacility().getId()) == 1) {
            throw new DataException("Facility to Requisition Group mapping already exists");
        }

        List<Integer> programIDsForRG = requisitionGroupProgramScheduleMapper.getProgramIDsById(requisitionGroupMember.getRequisitionGroup().getId());
        if (programIDsForRG.size() == 0) {
            throw new DataException("No Program(s) mapped for Requisition Group");
        }

        List<Integer> commonProgramsId = intersection(requisitionGroupProgramIdsForFacility, programIDsForRG);
        if (commonProgramsId.size() > 0) {
            Program duplicateProgram = programMapper.getById(commonProgramsId.get(0));
            duplicateProgram.setId(commonProgramsId.get(0));
            throw new DataException("Facility " + requisitionGroupMember.getFacility().getCode() + " is already assigned to Requisition Group " +
                    requisitionGroupMemberMapper.getRequisitionGroupCodeForProgramAndFacility(duplicateProgram.getId(), requisitionGroupMember.getFacility().getId())
                    + " running same program " + duplicateProgram.getCode());
        }

        requisitionGroupMemberMapper.insert(requisitionGroupMember);
    }

}
