package org.openlmis.restapi.builder;


import org.openlmis.core.domain.Product;
import org.openlmis.restapi.domain.ProgramWithProducts;

import java.util.ArrayList;
import java.util.List;

public class ProgramWithProductsBuilder {

    private String programName = "Mutants";
    private String programCode = "X-MEN";
    private List<Product> products = new ArrayList();

    public ProgramWithProductsBuilder withProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public ProgramWithProductsBuilder withProgramCode(String programCode) {
        this.programCode = programCode;
        return this;
    }

    public ProgramWithProductsBuilder addProduct(Product product) {
        this.products.add(product);
        return this;
    }

    public ProgramWithProducts build() {
        ProgramWithProducts instance = new ProgramWithProducts();
        instance.setProgramCode(programCode);
        instance.setProgramName(programName);
        instance.setProducts(products);
        return instance;
    }
}
