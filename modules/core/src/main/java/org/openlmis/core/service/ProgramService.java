package org.openlmis.core.service;

import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {

    public ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<Program> getAll() {
        return programRepository.getAll();
    }

    public List<Program> getByFacilityCode(String facilityCode) {
        return programRepository.getByFacilityCode(facilityCode);
    }
}
