package org.openlmis.restapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.LotOnHand;
import org.openlmis.stockmanagement.domain.StockCard;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockCardDTO {

    Product product;

    private List<StockCardMovementDTO> stockMovementItems;

    long stockOnHand;

    private List<LotOnHand> lotsOnHand;


    public StockCardDTO(StockCard stockCard) {
        this.product = initProduct(stockCard.getProduct());
        this.stockOnHand = stockCard.getTotalQuantityOnHand();
        this.stockMovementItems = new ArrayList<>();
        this.lotsOnHand = stockCard.getLotsOnHand();
    }

    private Product initProduct( Product StockCardProduct) {
        Product product = new Product();
        product.setId(StockCardProduct.getId());
        product.setCode(StockCardProduct.getCode());
        return product;
    }
}
