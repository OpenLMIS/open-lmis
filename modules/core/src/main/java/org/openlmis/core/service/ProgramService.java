package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ProgramService {

    public ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<Program> getAllActive() {
        return programRepository.getAllActive();
    }

    public List<Program> getByFacility(Long facilityId) {
        return programRepository.getByFacility(facilityId);
    }

    public List<Program> getAll() {
        return programRepository.getAll();
    }

    public List<Program> filterActiveProgramsAndFacility(List<RoleAssignment> roleAssignments, Long facilityId) {
        return programRepository.filterActiveProgramsAndFacility(roleAssignments, facilityId);
    }
}