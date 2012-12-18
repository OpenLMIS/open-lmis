package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
// TODO : rename to FacilityTypeApprovedProduct
public class FacilityApprovedProduct {

    private int id;

    // TODO : change this to id
    private String facilityTypeCode;

    private ProgramProduct programProduct;

    private Integer maxMonthsOfStock;

    private String modifiedBy;

    private Date modifiedDate;

    public FacilityApprovedProduct(String facilityTypeCode, ProgramProduct programProduct, Integer maxMonthsOfStock) {
        this.facilityTypeCode = facilityTypeCode;
        this.maxMonthsOfStock = maxMonthsOfStock;
        this.setProgramProduct(programProduct);
    }

}
