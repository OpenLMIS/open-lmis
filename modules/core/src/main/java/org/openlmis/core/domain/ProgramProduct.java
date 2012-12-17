package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProduct implements Importable {

    private Integer id;

    @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
    private Program program;
    @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
    private Product product;

    // TODO : should change this to id.
    private String programCode;
    private String productCode;
    @ImportField (name ="Doses Per Month", type="int", mandatory = true)
    private Integer dosesPerMonth;
    private String modifiedBy;
    private Date modifiedDate;
    private boolean active;

    public ProgramProduct(String programCode, String productCode, Integer dosesPerMonth) {
        this.programCode = programCode;
        this.productCode = productCode;
        this.dosesPerMonth = dosesPerMonth;
    }

    public ProgramProduct(String programCode, Product product, Integer dosesPerMonth) {
        this(programCode, product.getCode(), dosesPerMonth);
        this.product = product;
    }

    public ProgramProduct(Program program, Product product) {
        this.program = program;
        this.product = product;
    }

}
