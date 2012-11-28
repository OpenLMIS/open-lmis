package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
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

    public List<Program> getByFacilityCode(String facilityCode) {
        return programRepository.getByFacilityCode(facilityCode);
    }

    public List<Program> getAll() {
        return programRepository.getAll();
    }

}