package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionColumns;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineOrderRequisitionsColumnService {

    @Autowired
    VaccineOrderRequisitionColumnRepository columnRepository;

    public List<VaccineOrderRequisitionColumns> getAllColumns(){
        return columnRepository.getAllColumns();
    }


}
