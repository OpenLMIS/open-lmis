package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.ShipmentFileDetail;
import org.openlmis.core.repository.mapper.BudgetMapper;
import org.openlmis.core.repository.mapper.ShipmentFileDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class ShipmentFileDetailRepository {

   @Autowired
   private ShipmentFileDetailMapper detailMapper;

   public ShipmentFileDetail getByReferenceCodes(String orderId, String productCode){
     return detailMapper.getDetailByReferenceCodes(orderId, productCode);
   }

   public void insert(ShipmentFileDetail detail){
     detailMapper.insert(detail);
   }

   public void update(ShipmentFileDetail detail){
     detailMapper.update(detail);
   }
}
