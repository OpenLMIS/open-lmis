package org.openlmis.vaccine.repository.VaccineOrderRequisitions;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionLineItem;
import org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineOrderRequisitionLineItemsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccineOrderRequisitionLineItemRepository {
    @Autowired
    VaccineOrderRequisitionLineItemsMapper lineItemsMapper;

    public void Insert(VaccineOrderRequisitionLineItem lineItem){
        lineItemsMapper.Insert(lineItem);
    }

    public void Update(VaccineOrderRequisitionLineItem lineItem){
        lineItemsMapper.Update(lineItem);
    }

    public void insert(VaccineOrderRequisitionLineItem item){
        lineItemsMapper.insert(item);
    }
}
