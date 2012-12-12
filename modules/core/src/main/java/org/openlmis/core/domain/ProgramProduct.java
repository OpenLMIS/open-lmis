package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProduct {

    private String programCode;
    private String productCode;
    private Integer dosesPerMonth;
    private String modifiedBy;
    private Date modifiedDate;
    private boolean active;

    public ProgramProduct(String programCode, String productCode, Integer dosesPerMonth) {
        this.programCode = programCode;
        this.productCode = productCode;
        this.dosesPerMonth = dosesPerMonth;
    }
}
