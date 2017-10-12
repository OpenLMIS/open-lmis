package org.openlmis.programs.domain.malaria;

import lombok.Data;
import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateProduct;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Treatment {
    private int id;
    @NotNull
    @ValidateProduct
    private Product product;
    @Min(0)
    private int amount;
    @Min(0)
    private int stock;
    private Implementation implementation;

    public Treatment(Product product, int amount, int stock) {
        this.product = product;
        this.amount = amount;
        this.stock = stock;
    }
}
