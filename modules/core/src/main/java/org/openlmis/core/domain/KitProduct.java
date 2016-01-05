package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class KitProduct extends BaseModel {

    private Kit kit;
    private Product product;
    private Integer quantity;

    public KitProduct(Kit kit, Product product, Integer quantity) {
        this.kit = kit;
        this.product = product;
        this.quantity = quantity;
    }
}
