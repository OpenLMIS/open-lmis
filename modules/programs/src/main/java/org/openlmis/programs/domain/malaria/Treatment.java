package org.openlmis.programs.domain.malaria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateProduct;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Treatment {
    private int id;
    @NotNull
    @ValidateProduct
    private Product product;
    @Min(0)
    private int amount;
    @Min(0)
    private int stock;
    @JsonIgnore
    private Implementation implementation;

    public Treatment(Product product, int amount, int stock) {
        this.product = product;
        this.amount = amount;
        this.stock = stock;
    }
}
