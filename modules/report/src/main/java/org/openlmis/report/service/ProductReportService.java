package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.ProductReportMapper;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openlmis.report.model.dto.*;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:47 AM
 */
@Service
@NoArgsConstructor
public class ProductReportService {

    @Autowired
    private ProductReportMapper productMapper;
    public ProductReportService(ProductReportMapper productMapper){
        this.productMapper = productMapper;
    }

    public List<Product> getAllProducts(){
        return productMapper.getAll();
    }
}
