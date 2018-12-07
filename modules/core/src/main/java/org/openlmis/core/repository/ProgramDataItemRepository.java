package org.openlmis.core.repository;

import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.repository.mapper.ProgramDataColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProgramDataItemRepository {

    @Autowired
    private ProgramDataColumnMapper programDataColumnMapper;

    public List<ProgramDataColumn> getAll() {
        return programDataColumnMapper.getAll();
    }
}
