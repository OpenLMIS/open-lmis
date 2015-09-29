package org.openlmis.vaccine.repository.VaccineOrderRequisitions;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionColumns;
import org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineOrderRequisitionColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineOrderRequisitionColumnRepository {
    @Autowired
    VaccineOrderRequisitionColumnMapper mapper;

    public List<VaccineOrderRequisitionColumns> getAllColumns(){
        return mapper.getColumns();
    }

}
