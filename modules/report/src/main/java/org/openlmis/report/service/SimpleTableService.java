package org.openlmis.report.service;

import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.report.mapper.ProductLotInfoMapper;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.params.NonSubmittedRequisitionReportsParam;
import org.openlmis.report.model.params.OverStockReportParam;
import org.openlmis.report.model.params.RequisitionReportsParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleTableService {
    @Autowired
    private RequisitionReportsMapper requisitionReportsMapper;

    @Autowired
    private ProductLotInfoMapper productLotInfoMapper;

    @Autowired
    private FacilityMapper facilityMapper;
    protected static Logger logger = LoggerFactory.getLogger(SimpleTableService.class);

    public List<RequisitionDTO> getRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        requisitions.addAll(getSubmittedRequisitions(filterCriteria));
        requisitions.addAll(getUnSubmittedRequisitions(filterCriteria));

        return requisitions;
    }

    private List<RequisitionDTO> getSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = requisitionReportsMapper.getSubmittedRequisitionList(filterCriteria);
        return requisitions;
    }


    public List<OverStockProductDto> getOverStockProductReport(OverStockReportParam filterCriteria) {
        List<ProductLotInfo> productLotInfos = productLotInfoMapper.getProductLotInfoList(filterCriteria);
        if(CollectionUtils.isEmpty(productLotInfos)){
            return null;
        }
        Map<String,OverStockProductDto> overStockProductDtoMap = overStockProductGroupBy(productLotInfos);
        List<OverStockProductDto> overStockProducts = new ArrayList<>();
        OverStockProductDto overStockProduct;
        for (Map.Entry<String,OverStockProductDto> entry : overStockProductDtoMap.entrySet()) {
            overStockProduct = entry.getValue();
//            long cmm
//            long soh
//            String productCode
//            StockOnHandStatusCalculation.getStockOnHandStatus();
            overStockProduct.setCmm(calcCmm());
            overStockProduct.setMos(calcMos());
            overStockProducts.add(overStockProduct);
        }

        return overStockProducts;
    }


    private Map<String,OverStockProductDto> overStockProductGroupBy(List<ProductLotInfo> productLotInfos){
        Map<String,OverStockProductDto> overStockProductDtoMap = new HashMap<>();
        String key;
        for (ProductLotInfo lotInfo : productLotInfos){
            key = lotInfo.getProvinceId()+"-"+lotInfo.getDistrictId()+"-"+lotInfo.getFacilityId()+"-"+lotInfo.getProductCode();
            logger.error(lotInfo.toString());
            logger.error(key);
            if(overStockProductDtoMap.containsKey(key)){
                overStockProductDtoMap.get(key).getLotInfo().add(new LotInfo(lotInfo.getLotNumber(),lotInfo.getExpiryDate(),lotInfo.getStockOnHandOfLot()));
            }else{
                overStockProductDtoMap.put(key,OverStockProductDto.of(lotInfo));
            }
        }
        return overStockProductDtoMap;
    }

    private double calcCmm(){
        //todo
        return 7;
    }

    private double calcMos(){
        //todo
        return 3;
    }

    private List<RequisitionDTO> getUnSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        List<Integer> facilityIds = getFacilityIds(filterCriteria);
        NonSubmittedRequisitionReportsParam nonSubmittedRequisitionReportsParam;
        for(Integer facilityId : facilityIds) {
            for(MonthlyReortProgramType programType : MonthlyReortProgramType.values()) {
                Integer programId = programType.getProgramId();

                if(filterCriteria.getProgramIds().contains(programId)) {
                    nonSubmittedRequisitionReportsParam = NonSubmittedRequisitionReportsParam.builder()
                            .startTime(filterCriteria.getStartTime())
                            .endTime(filterCriteria.getEndTime())
                            .facilityId(facilityId)
                            .programId(programId)
                            .build();
                    requisitions.addAll(requisitionReportsMapper.getUnSubmittedRequisitionList(nonSubmittedRequisitionReportsParam));
                }
            }
        }

        return requisitions;
    }

    private List<Integer> getFacilityIds(RequisitionReportsParam filterCriteria) {
        List<Integer> facilityIds = new ArrayList<>();

        if(null != filterCriteria.getFacilityId()) {
            facilityIds.add(filterCriteria.getFacilityId());
            return facilityIds;
        }

        if(null != filterCriteria.getDistrictId()) {
            facilityIds = facilityMapper.getFacilityIdByDistrictId(filterCriteria.getDistrictId());
            return facilityIds;
        }

        if(null != filterCriteria.getProvinceId()) {
            facilityIds = facilityMapper.getFacilityIdByProvinceId(filterCriteria.getProvinceId());
            return facilityIds;
        }

        facilityIds = facilityMapper.getAllFacilityIds();
        return facilityIds;
    }
}