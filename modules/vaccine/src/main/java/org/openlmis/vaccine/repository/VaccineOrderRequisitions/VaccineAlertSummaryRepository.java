package org.openlmis.vaccine.repository.VaccineOrderRequisitions;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineAlertSummary;
import org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineAlertSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineAlertSummaryRepository {
    @Autowired
    VaccineAlertSummaryMapper mapper;

    public List<VaccineAlertSummary>getAlerts(Long zoneId,Long programId, Long periodId,Long userId ){

        return mapper.getAlerts(zoneId,programId,periodId,userId);

    }

}
