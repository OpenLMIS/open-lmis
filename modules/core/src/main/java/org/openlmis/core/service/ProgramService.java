package org.openlmis.core.service;

import org.openlmis.core.dao.ProgramMapper;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {

    @Autowired
    public ProgramMapper programMapper;

    public ProgramService() {
    }

    public ProgramService(ProgramMapper programMapper) {
        this.programMapper = programMapper;
    }

    public List<Program> getAll() {
        return programMapper.selectAll();
    }

    public void add(Program program) {
        programMapper.insert(program);
    }

    public void removeAll() {
        programMapper.deleteAll();
    }

}
