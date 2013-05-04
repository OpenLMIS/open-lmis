package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.ProductCategoryReportMapper;
import org.openlmis.report.mapper.ProductReportMapper;
import org.openlmis.report.mapper.RequisitionGroupReportMapper;
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
public class ReportLookupService {


    private ProductReportMapper productMapper;
    private RequisitionGroupReportMapper rgMapper;
    private ProductCategoryReportMapper productCategoryMapper;

    @Autowired
    public ReportLookupService(ProductReportMapper productMapper, RequisitionGroupReportMapper rgMapper, ProductCategoryReportMapper productCategoryMapper){
        this.productMapper = productMapper;
        this.rgMapper = rgMapper;
        this.productCategoryMapper = productCategoryMapper;
    }

    public List<Product> getAllProducts(){
        return productMapper.getAll();
    }

    public List<RequisitionGroup> getAllRequisitionGroups(){
        return this.rgMapper.getAll();
    }

    public List<ProductCategory> getAllProductCategories(){
        return this.productCategoryMapper.getAll();
    }


}
