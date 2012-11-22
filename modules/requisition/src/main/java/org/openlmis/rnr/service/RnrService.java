package org.openlmis.rnr.service;

import org.joda.time.DateTime;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.rnr.dao.RnrRepository;
import org.openlmis.rnr.domain.Requisition;
import org.openlmis.rnr.domain.RequisitionLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RnrService {

    private RnrRepository rnrRepository;

    private ProductService productService;

    @Autowired
    public RnrService(RnrRepository rnrRepository, ProductService productService) {
        this.rnrRepository = rnrRepository;
        this.productService = productService;
    }

    public Requisition initRnr(String facilityCode, String programCode) {
        Requisition requisition = new Requisition(facilityCode, programCode, RnrStatus.INITIATED, "user", DateTime.now().toDate());
        int rnrId = rnrRepository.insert(requisition);
        requisition.setId(rnrId);

        List<Product> products = productService.getByFacilityAndProgram(facilityCode, programCode);
        for (Product product : products) {
            requisition.add(new RequisitionLineItem(rnrId, product.getCode()));
        }

        return requisition;
    }

}
