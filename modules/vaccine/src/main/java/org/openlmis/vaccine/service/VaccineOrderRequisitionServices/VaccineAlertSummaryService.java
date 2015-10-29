package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineAlertSummary;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineAlertSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineAlertSummaryService {
    @Autowired
    VaccineAlertSummaryRepository repository;

    public List<VaccineAlertSummary>getAlerts(Long zoneId, Long programId, Long periodId,Long userId){
        return  repository.getAlerts(zoneId,programId,periodId,userId);
    }

}
