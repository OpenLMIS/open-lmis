package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProduct {

    // TODO : should change this to id.
    private String programCode;
    private String productCode;
    private Integer dosesPerMonth;
    private String modifiedBy;
    private Date modifiedDate;
    private boolean active;
    private Product product;

    public ProgramProduct(String programCode, String productCode, Integer dosesPerMonth) {
        this.programCode = programCode;
        this.productCode = productCode;
        this.dosesPerMonth = dosesPerMonth;
    }

    public ProgramProduct(String programCode, Product product, Integer dosesPerMonth) {
        this(programCode, product.getCode(), dosesPerMonth);
        this.product = product;
    }

}
