package org.openlmis.restapi.service;

import org.openlmis.restapi.domain.RequisitionServiceResponse;
import org.openlmis.restapi.mapper.RestRequisitionServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RestRequisitionServiceService {

    @Autowired
    private RestRequisitionServiceMapper restRequisitionServiceMapper;

    public List<RequisitionServiceResponse> getRequisitionServices(Date afterUpdatedTimeInDate, String programCode) {

        List<RequisitionServiceResponse> requisitionServices = restRequisitionServiceMapper.getRequisitonServices(afterUpdatedTimeInDate, programCode);

        return requisitionServices;
    }
}