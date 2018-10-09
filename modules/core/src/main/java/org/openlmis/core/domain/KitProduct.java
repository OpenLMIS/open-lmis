package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class KitProduct extends BaseModel {

    private String kitCode;
    private String productCode;
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KitProduct that = (KitProduct) o;
        return Objects.equals(kitCode, that.kitCode) &&
                Objects.equals(productCode, that.productCode) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(kitCode, productCode, quantity);
    }

    @Override
    public String toString() {
        return "KitProduct{" +
                "kitCode='" + kitCode + '\'' +
                ", productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
