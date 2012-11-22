package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.Requisition;
import org.openlmis.rnr.repository.mapper.RnrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RnrRepository {

    private RnrMapper rnrMapper;

    @Autowired
    public RnrRepository(RnrMapper rnrMapper) {
        this.rnrMapper = rnrMapper;
    }

    public int insert(Requisition requisition) {
        return rnrMapper.insert(requisition);
    }

}
