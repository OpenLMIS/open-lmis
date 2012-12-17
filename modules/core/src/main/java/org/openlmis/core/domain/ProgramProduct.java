package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProduct implements Importable {

    @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
    private Program program;
    @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
    private Product product;
    @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
    private Integer dosesPerMonth;
    @ImportField(name = "Is Active", type = "boolean", mandatory = true)
    private boolean active;

    private Integer id;
    private String modifiedBy;
    private Date modifiedDate;

    public ProgramProduct(Program program, Product product, Integer dosesPerMonth, Boolean active) {
        this.program = program;
        this.product = product;
        this.dosesPerMonth = dosesPerMonth;
        this.active = active;
    }
}
