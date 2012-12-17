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
    @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
    private Integer dosesPerMonth;

    private String modifiedBy;
    private Date modifiedDate;
    private boolean active;

    public ProgramProduct(Program program, Product product, Integer dosesPerMonth) {
        this.program = program;
        this.product = product;
        this.dosesPerMonth = dosesPerMonth;
    }
}
