package org.openlmis.vaccine.domain.VaccineOrderRequisition;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineOrderRequisition extends BaseModel {
    public static final SimpleDateFormat form = new SimpleDateFormat("YYYY-MM-dd");
    private Long periodId;
    private Long programId;
    private ProductCategory productCategory;
    private VaccineOrderStatus status;
    private ProcessingPeriod period;
    private Facility facility;
    private Program program;
    private Long supervisoryNodeId;
    private Long facilityId;
    private String orderDate;
    private boolean emergency;
    private List<VaccineOrderRequisitionLineItem> lineItems;
    private List<VaccineOrderRequisitionStatusChange> statusChanges;
    private List<OrderRequisitionDTO> orderRequisitionDTOs;
    private List<OrderRequisitionStockCardDTO> stockCards;
    private List<VaccineOrderRequisitionColumns> columnsList;

    public void viewOrderRequisitionLineItems(List<OrderRequisitionStockCardDTO> stockCards) {
        lineItems = new ArrayList<>();

        ISA myIsa;

           for (OrderRequisitionStockCardDTO stockCard : stockCards) {

               myIsa = new ISA.Builder().adjustmentValue(stockCard.getAdjustmentValue()).bufferPercentage(stockCard.getBufferPercentage()).dosesPerYear(stockCard.getDosesPerYear()).
                       maximumValue(stockCard.getMaximumValue())
                       .minimumValue(stockCard.getMinimumValue()).wastageFactor(stockCard.getWastageFactor()).whoRatio(stockCard.getWhoRatio()).build();

               VaccineOrderRequisitionLineItem lineItem = new VaccineOrderRequisitionLineItem();

               lineItem.setOrderId(id);
               lineItem.setProductId(stockCard.getProduct().getId());
               lineItem.setProductName(stockCard.getProduct().getPrimaryName());
               lineItem.setMaxmonthsofstock(stockCard.getMaxmonthsofstock());
               lineItem.setOverriddenisa(myIsa.calculate(facility.getCatchmentPopulation()));
               lineItem.setEop(stockCard.getEop());
               lineItem.setStockOnHand(stockCard.getTotalQuantityOnHand());
               lineItem.setMinMonthsOfStock(stockCard.getMinmonthsofstock());
               lineItem.setOrderedDate(form.format(new Date()));

               lineItems.add(lineItem);
           }

    }


    public void initiateOrderRequisitionLineItem(List<ProgramProduct>programProducts){
        orderRequisitionDTOs = new ArrayList<>();
        for(ProgramProduct pp: programProducts){
            OrderRequisitionDTO lineItem = new OrderRequisitionDTO();
            lineItem.setProductCategory(pp.getProductCategory().getName());
            orderRequisitionDTOs.add(lineItem);
        }
    }

    public void initColumns(List<VaccineOrderRequisitionColumns> columns){
        columnsList = new ArrayList<>();
        for(VaccineOrderRequisitionColumns columns1: columns){
            VaccineOrderRequisitionColumns lineItem = new VaccineOrderRequisitionColumns();
            lineItem.setName(lineItem.getName());
            lineItem.setLabel(lineItem.getLabel());
            lineItem.setDisplayOrder(columns1.getDisplayOrder());
            columnsList.add(lineItem);
        }

    }

}
