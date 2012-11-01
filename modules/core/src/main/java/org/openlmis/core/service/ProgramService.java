package org.openlmis.core.service;

import org.openlmis.core.dao.ProgramMapper;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {

    public ProgramMapper programMapper;

    @Autowired
    public ProgramService(ProgramMapper programMapper) {
        this.programMapper = programMapper;
    }

    public List<Program> getAll() {
        return programMapper.selectAll();
    }
}
