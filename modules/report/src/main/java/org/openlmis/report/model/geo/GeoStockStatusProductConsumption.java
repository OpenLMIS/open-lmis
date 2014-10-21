package org.openlmis.report.model.geo;

/**
 * Created by issa on 10/17/14.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoStockStatusProductConsumption {

    private Long productId;

    private String productName;

    private Long periodId;

    private String periodName;

    private Integer periodYear;

    private Integer quantityOnHand;

    private Integer quantityConsumed;

    private Integer amc;
}
