package org.openlmis.report.model.dto;

import lombok.*;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductLotInfo {
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private Integer facilityId;
    private String facilityName;
    private String facilityCode;
    private Integer productId;
    private String productCode;
    private String productName;
    private String lotNumber;
    private Date expiryDate;
    private Integer stockOnHandOfLot;
    private Boolean isHiv;
    private Date lastSyncDate;
    private Double price;

    public String getLastSyncDateString() {
        if (null != lastSyncDate) {
            return DateUtil.formatDate(lastSyncDate);
        }
        return  "";
    }

    public String getDistirctKey() {
        return this.getProvinceId() + "-" + this.getDistrictId() +  "-" + this.getProductCode();
    }

    public String getFacilityKey() {
        return this.getProvinceId() + "-" + this.getDistrictId() + "-" + this.getFacilityId() + "-" + this.getProductCode();
    }
}