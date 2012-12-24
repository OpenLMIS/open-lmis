package org.openlmis.core.repository;

import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class SupplyLineRepository {

    private SupplyLineMapper supplyLineMapper;

    @Autowired
    public SupplyLineRepository(SupplyLineMapper supplyLineMapper) {
        this.supplyLineMapper = supplyLineMapper;
    }

    public void insert(SupplyLine supplyLine) {
        try {
            supplyLineMapper.insert(supplyLine);
        } catch (DuplicateKeyException ex) {
            throw new DataException("Duplicate entry for Supply Line found.");
        }
    }


}
