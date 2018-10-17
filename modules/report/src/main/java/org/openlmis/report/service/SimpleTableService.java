package org.openlmis.report.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.report.generator.StockOnHandStatus;
import org.openlmis.report.mapper.ProductLotInfoMapper;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.params.NonSubmittedRequisitionReportsParam;
import org.openlmis.report.model.params.OverStockReportParam;
import org.openlmis.report.model.params.RequisitionReportsParam;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.repository.mapper.CMMMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SimpleTableService {
    @Autowired
    private RequisitionReportsMapper requisitionReportsMapper;

    @Autowired
    private ProductLotInfoMapper productLotInfoMapper;

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private CMMMapper cmmMapper;

    @Autowired
    private StockStatusService stockStatusService;

    protected static Logger logger = LoggerFactory.getLogger(SimpleTableService.class);

    public List<RequisitionDTO> getRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        requisitions.addAll(getSubmittedRequisitions(filterCriteria));
        requisitions.addAll(getUnSubmittedRequisitions(filterCriteria));

        for (RequisitionDTO requisitionDTO : requisitions) {
            requisitionDTO.assignType();
        }

        return requisitions;
    }

    private List<RequisitionDTO> getSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = requisitionReportsMapper.getSubmittedRequisitionList(filterCriteria);
        return requisitions;
    }


    public List<OverStockProductDto> getOverStockProductReport(OverStockReportParam filterCriteria) {
        List<ProductLotInfo> productLotInfos = productLotInfoMapper.getProductLotInfoList(filterCriteria);
        if (CollectionUtils.isEmpty(productLotInfos)) {
            return null;
        }

        List<OverStockProductDto> overStockProducts = new ArrayList<>();
        Map<String, OverStockProductDto> overStockProductDtoMap = overStockProductGroupBy(productLotInfos, filterCriteria);
        Map<String, CMMEntry> cmmEntryMap = getProductCmmMap(filterCriteria);
        OverStockProductDto overStockProduct;
        for (Map.Entry<String, OverStockProductDto> entry : overStockProductDtoMap.entrySet()) {
            overStockProduct = entry.getValue();
            overStockProduct = calcCmmAndSoh(overStockProduct, cmmEntryMap, filterCriteria);
            if (null != overStockProduct) {
                overStockProducts.add(overStockProduct);
            }
        }
        return overStockProducts;
    }

    private OverStockProductDto calcCmmAndSoh(OverStockProductDto overStockProduct, Map<String, CMMEntry> cmmEntryMap, OverStockReportParam filterCriteria) {
        if (CollectionUtils.isEmpty(overStockProduct.getLotList())) {
            return null;
        }
        CMMEntry cmmEntry = cmmEntryMap.get(overStockProduct.getProductCode());
        if (null == cmmEntry || null == cmmEntry.getCmmValue()) {
            return null;
        }
        Integer sumSoH = OverStockProductDto.calcSoH(overStockProduct.getLotList());
        StockOnHandStatus status = stockStatusService.getStockOnHandStatus(cmmEntry.getCmmValue().longValue(), sumSoH, overStockProduct.getProductCode());
        if (!status.equals(StockOnHandStatus.OVER_STOCK)) {
            return null;
        }

        if(null == filterCriteria.getDistrictId()){
            return overStockProduct;
        }

        Double cmm = cmmEntry.getCmmValue().doubleValue();
        overStockProduct.setCmm(Double.valueOf(cmm.doubleValue()));
        if (0 != cmm) {
            overStockProduct.setMos(sumSoH / cmm);
        }
        return overStockProduct;
    }

    private Map<String, CMMEntry> getProductCmmMap(OverStockReportParam filterCriteria) {
        List<CMMEntry> CMMEntryList = new ArrayList<>();
//        if(null != filterCriteria.getDistrictId()){
//            CMMEntryList = cmmMapper.getCMMEntryByDistrictAndDay(filterCriteria.getDistrictId().longValue(), filterCriteria.getEndTime());
//        }else{
//            CMMEntryList = cmmMapper.getCMMEntryByProvinceAndDay(filterCriteria.getProvinceId().longValue(), filterCriteria.getEndTime());
//        }

        Map<String, CMMEntry> cmmEntryMap = new HashMap<>();
        for (CMMEntry cmmEntry : CMMEntryList) {
            cmmEntryMap.put(cmmEntry.getProductCode(), cmmEntry);
        }
        return cmmEntryMap;
    }


    private Map<String, OverStockProductDto> overStockProductGroupBy(List<ProductLotInfo> productLotInfos, OverStockReportParam filterCriteria) {
        Map<String, OverStockProductDto> overStockProductDtoMap = new HashMap<>();
        String key;
        LotInfo lotinfo;
        for (ProductLotInfo lotInfo : productLotInfos) {
            key = lotInfo.getProvinceId() + "-" + lotInfo.getDistrictId() + "-" + lotInfo.getFacilityId() + "-" + lotInfo.getProductCode();
            lotinfo = new LotInfo(lotInfo.getLotNumber(), lotInfo.getExpiryDate(), lotInfo.getStockOnHandOfLot());
            if (overStockProductDtoMap.containsKey(key)) {
                overStockProductDtoMap.get(key).getLotList().add(lotinfo);
            } else {
                OverStockProductDto dto = OverStockProductDto.of(lotInfo);
                dto.getLotList().add(lotinfo);
                overStockProductDtoMap.put(key, dto);
            }
        }
        return overStockProductDtoMap;
    }

    private List<RequisitionDTO> getUnSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        List<Integer> facilityIds = getFacilityIds(filterCriteria);
        NonSubmittedRequisitionReportsParam nonSubmittedRequisitionReportsParam;
        for (Integer facilityId : facilityIds) {
            for (MonthlyReortProgramType programType : MonthlyReortProgramType.values()) {
                Integer programId = programType.getProgramId();

                if (filterCriteria.getProgramIds().contains(programId)) {
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

        if (null != filterCriteria.getFacilityId()) {
            facilityIds.add(filterCriteria.getFacilityId());
            return facilityIds;
        }

        if (null != filterCriteria.getDistrictId()) {
            facilityIds = facilityMapper.getFacilityIdByDistrictId(filterCriteria.getDistrictId());
            return facilityIds;
        }

        if (null != filterCriteria.getProvinceId()) {
            facilityIds = facilityMapper.getFacilityIdByProvinceId(filterCriteria.getProvinceId());
            return facilityIds;
        }

        facilityIds = facilityMapper.getAllFacilityIds();
        return facilityIds;
    }
}