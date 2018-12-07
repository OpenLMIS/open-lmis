package org.openlmis.core.repository;

import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.repository.mapper.ProgramDataColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProgramDataColumnRepository {

    @Autowired
    private ProgramDataColumnMapper programDataColumnMapper;

    public List<ProgramDataColumn> getAll() {
        return programDataColumnMapper.getAll();
    }


}
