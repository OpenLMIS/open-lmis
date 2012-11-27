package org.openlmis.rnr.service;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.RnrLineItemRepository;
import org.openlmis.rnr.repository.RnrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.joda.time.DateTime.now;

@Service
public class RnrService {

    private RnrRepository rnrRepository;

    private RnrLineItemRepository rnrLineItemRepository;

    private ProductService productService;

    private RnrTemplateService rnrTemplateService;

    public RnrService() {
    }

    @Autowired
    public RnrService(RnrRepository rnrRepository, RnrLineItemRepository rnrLineItemRepository, ProductService productService, RnrTemplateService rnrTemplateService) {
        this.rnrRepository = rnrRepository;
        this.rnrLineItemRepository = rnrLineItemRepository;
        this.productService = productService;
        this.rnrTemplateService = rnrTemplateService;
    }

    @Transactional
    public Rnr initRnr(String facilityCode, String programCode, String modifiedBy) {
        Rnr requisition = new Rnr(facilityCode, programCode, RnrStatus.INITIATED, modifiedBy);
        rnrRepository.insert(requisition);

        List<RnrColumn> programRnrTemplate = rnrTemplateService.fetchVisibleRnRColumns(programCode);
        List<Product> products = productService.getByFacilityAndProgram(facilityCode, programCode);
        for (Product product : products) {
            RnrLineItem requisitionLineItem = new RnrLineItem(requisition.getId(), product, modifiedBy);
            requisitionLineItem.createFieldsBy(programRnrTemplate);
            rnrLineItemRepository.insert(requisitionLineItem);
            requisition.add(requisitionLineItem);
        }
        return requisition;
    }

}
