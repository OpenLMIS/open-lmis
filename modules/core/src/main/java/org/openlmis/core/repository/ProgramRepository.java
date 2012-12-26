package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class ProgramRepository {

    private ProgramMapper programMapper;
    private ProgramSupportedMapper programSupportedMapper;
    private RoleRightsMapper roleRightsMapper;

    @Autowired
    public ProgramRepository(ProgramMapper programMapper, ProgramSupportedMapper programSupportedMapper, RoleRightsMapper roleRightsMapper) {
        this.programMapper = programMapper;
        this.programSupportedMapper = programSupportedMapper;
        this.roleRightsMapper = roleRightsMapper;
    }

    public List<Program> getAllActive() {
        return programMapper.getAllActive();
    }

    public List<Program> getByFacility(Integer facilityId) {
        return programMapper.getActiveByFacility(facilityId);
    }

    public List<Program> getAll() {
        return programMapper.getAll();
    }

    public List<Program> getUserSupervisedActivePrograms(String userName, Right right) {
        return programMapper.getUserSupervisedActivePrograms(userName, right);
    }

    public List<Program> filterActiveProgramsAndFacility(List<RoleAssignment> roleAssignments, Integer facilityId) {
        String programIds = getCommaSeparatedProgramsForDB(roleAssignments);
        return programSupportedMapper.filterActiveProgramsAndFacility(programIds, facilityId);
    }

    public Integer getIdForCode(String code) {
        Integer programId = programMapper.getIdForCode(code);

        if (programId == null)
            throw new DataException("Invalid Program Code");

        return programId;
    }

    private String getCommaSeparatedProgramsForDB(List<RoleAssignment> roleAssignments) {
        List<Integer> programIds = new ArrayList<>();
        for (RoleAssignment roleAssignment : roleAssignments) {
            programIds.add(roleAssignment.getProgramId());
        }
        return programIds.toString().replace("[", "{").replace("]", "}");
    }
}
