package org.openlmis.rnr.service;

import org.joda.time.DateTime;
import org.openlmis.rnr.dao.RnrRepository;
import org.openlmis.rnr.domain.Requisition;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RnrService {

    private RnrRepository rnrRepository;

    @Autowired
    public RnrService(RnrRepository rnrRepository) {
        this.rnrRepository = rnrRepository;
    }

    public int initRnr(String facilityCode, String programCode) {
        Requisition requisition = new Requisition(facilityCode, programCode, RnrStatus.INITIATED, "user", DateTime.now().toDate());
        return rnrRepository.insert(requisition);
    }

}
