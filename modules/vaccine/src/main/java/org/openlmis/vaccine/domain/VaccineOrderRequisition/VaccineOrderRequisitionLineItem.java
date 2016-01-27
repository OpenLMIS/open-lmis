package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccineOrderRequisitionLineItem extends BaseModel {

    private Long orderId;
    private Long productId;
    private String productName;
    private Product product;
    private ProductCategory productCategory;
    private Integer displayOrder;
    private Integer maximumStock;
    private Integer minimumStock;
    private Double reOrderLevel;
    private Double bufferStock;
    private Long stockOnHand;
    private Long quantityRequested;
    private String orderedDate;

    //will be used for calculation purpose on font end

    private Integer overriddenisa;
    private Integer maxmonthsofstock;
    private Double minMonthsOfStock;
    private Double eop;


}
