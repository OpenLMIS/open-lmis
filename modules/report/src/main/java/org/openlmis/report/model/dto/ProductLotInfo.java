package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLotInfo {
    private String provinceName;
    private String districtName;
    private String facilityName;
    private String productCode;
    private String productName;
    private String lotNumber;
    private Date expiryDate;
    private Integer stockOnHandOfLot;
}