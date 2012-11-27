package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.Rnr;
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

    public void insert(Rnr requisition) {
        int rnrId = rnrMapper.insert(requisition);
        requisition.setId(rnrId);
    }

}
