package org.openlmis.vaccine.repository.VaccineOrderRequisitions;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionStatusChange;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineStatusRequisitionChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineOrderRequisitionStatusChangeRepository {

    @Autowired
    private VaccineStatusRequisitionChangeMapper mapper;

    public void insert(VaccineOrderRequisitionStatusChange change){
        mapper.insert(change);
    }


    public List<VaccineOrderRequisitionStatusChange> getChangesForReport(Long orderId){
        return mapper.getChangeLogByReportId(orderId);
    }

    public VaccineOrderRequisitionStatusChange getOperation(Long reportId, VaccineOrderStatus status){
        return mapper.getOperationLog(reportId, status);
    }
}
