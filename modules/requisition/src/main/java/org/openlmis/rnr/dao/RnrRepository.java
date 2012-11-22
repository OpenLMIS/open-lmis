package org.openlmis.rnr.dao;

import org.openlmis.rnr.domain.Requisition;
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
