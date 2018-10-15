package org.openlmis.report.model.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OverStockProductDto {
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private Integer facilityId;
    private String facilityName;
    private Integer productId;
    private String productCode;
    private String productName;
    List<LotInfo> lotList = new ArrayList<>();
    private Double cmm;
    private Double mos;

    public static OverStockProductDto of(ProductLotInfo lotInfo){
        OverStockProductDto dto = new OverStockProductDto();

        dto.setProvinceId(lotInfo.getProvinceId());
        dto.setProvinceName(lotInfo.getProvinceName());
        dto.setDistrictId(lotInfo.getDistrictId());
        dto.setDistrictName(lotInfo.getDistrictName());
        dto.setFacilityId(lotInfo.getFacilityId());
        dto.setFacilityName(lotInfo.getFacilityName());
        dto.setProductId(lotInfo.getProductId());
        dto.setProductCode(lotInfo.getProductCode());
        dto.setProductName(lotInfo.getProductName());
        return dto;
    }

    public static Integer calcSoH(List<LotInfo> lotList){
        Integer sumSoH = 0;
        for(LotInfo lot : lotList){
            sumSoH = sumSoH + lot.getStockOnHandOfLot();
        }
        return sumSoH;
    }
}
