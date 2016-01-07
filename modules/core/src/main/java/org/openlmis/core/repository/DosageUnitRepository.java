package org.openlmis.core.repository;

import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DosageUnitRepository {
    @Autowired
    DosageUnitMapper dosageUnitMapper;

    public DosageUnit getByCode(String code) {
        return dosageUnitMapper.getByCode(code);
    }
}
