package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;

import java.util.*;

@Data
@NoArgsConstructor
public class RnrLineItem {

    private Integer id;
    private Integer rnrId;
    private Product product;

    private Integer quantityReceived;
    private Integer quantityDispensed ;
    private Integer beginningBalance;
    private Integer estimatedConsumption;
    private Integer stockInHand;
    private Integer quantityRequested;
    private String reasonForRequestedQuantity;
    private Integer calculatedOrderQuantity;

    private Integer quantityApproved;
    private Integer lossesAndAdjustments;
    private String reasonForLossesAndAdjustments;

    private Integer patientCount;
    private Integer stockOutDays;
    private float normalizedConsumption;
    private float amc;
    private String maxStockQuantity;

    private Integer packsToShip;
    private float cost;
    private String remarks;

    private String modifiedBy;
    private Date modifiedDate;
    private List<Object> fieldValuesByProgramTemplate = new ArrayList<>();
    private Map<String,Object> fieldMap = new HashMap<>();


    public RnrLineItem(Integer rnrId, Product product, String modifiedBy, Date modifiedDate) {
        this.rnrId = rnrId;
        this.product = product;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
        createFieldValuesMap();
    }

    private void createFieldValuesMap() {
        fieldMap.put("product_code", product.getCode());
        fieldMap.put("unit_of_issue", product.getDispensingUnit());
        fieldMap.put("beginning_balance",beginningBalance);
        fieldMap.put("quantity_received",quantityReceived);
        fieldMap.put("quantity_dispensed",quantityDispensed);
        fieldMap.put("losses_and_adjustments",lossesAndAdjustments);
        fieldMap.put("reason_for_losses_and_adjustments",reasonForLossesAndAdjustments);
        fieldMap.put("stock_in_hand",stockInHand);
        fieldMap.put("new_patient_count",patientCount);
        fieldMap.put("stock_out_days",stockOutDays);
        fieldMap.put("normalized_consumption",normalizedConsumption);
        fieldMap.put("amc",amc);
        fieldMap.put("max_stock_quantity",maxStockQuantity);
        fieldMap.put("calculated_order_quantity",calculatedOrderQuantity);
        fieldMap.put("quantity_requested",quantityRequested);
        fieldMap.put("reason_for_requested_quantity",reasonForRequestedQuantity);
        fieldMap.put("quantity_approved",quantityApproved);
        fieldMap.put("packs_to_ship",packsToShip);
//        fieldMap.put("Price",product.getPrice());
        fieldMap.put("cost",cost);
        fieldMap.put("remarks",remarks);
        fieldMap.put("product", product.getPrimaryName() + "  " + product.getProductForm().getName() + " " +
                product.getStrength() + " " + product.getProductDosageUnit().getName());
    }

    public void createFieldsBy(List<RnrColumn> programRnrColumns) {
       for(RnrColumn rnrColumn : programRnrColumns) {
            fieldValuesByProgramTemplate.add(fieldMap.get(rnrColumn.getName()));
       }
    }
}
