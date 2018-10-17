package org.openlmis.report.model.dto;

import lombok.*;

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
    private Integer productId;
    private String productCode;
    private String productName;
    private String lotNumber;
    private Date expiryDate;
    private Integer stockOnHandOfLot;
    private Boolean isHiv;

}