package org.openlmis.programs.domain.malaria;

import lombok.Data;
import org.openlmis.core.domain.Product;

@Data
public class Treatment {
    private int id;
    private Product product;
    private int amount;
    private int stock;
    private Implementation implementation;

    public Treatment(Product product, int amount, int stock) {
        this.product = product;
        this.amount = amount;
        this.stock = stock;
    }
}
