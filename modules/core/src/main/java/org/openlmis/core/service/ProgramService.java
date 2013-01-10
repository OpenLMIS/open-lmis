package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
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

    public List<Program> getByFacility(Integer facilityId) {
        return programRepository.getByFacility(facilityId);
    }

    public List<Program> getAll() {
        return programRepository.getAll();
    }

    public List<Program> getProgramsSupportedByFacilityForUserWithRight(Integer facilityId, Integer userId, Right... rights) {
        return programRepository.getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, rights);
    }

    public List<Program> getUserSupervisedActiveProgramsWithRights(Integer userId, Right... rights) {
        return programRepository.getUserSupervisedActiveProgramsWithRights(userId, rights);
    }
}