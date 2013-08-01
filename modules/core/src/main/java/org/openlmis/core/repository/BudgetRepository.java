package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.BudgetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openlmis.core.domain.Budget;


@Component
@NoArgsConstructor
public class BudgetRepository {

   @Autowired
   private BudgetMapper budgetMapper;

   public Budget getByReferenceCodes(String programCode, String periodName, String facilityCode){
        return budgetMapper.getBudgetByReferenceCodes(programCode, periodName, facilityCode );
   }

   public void insert(Budget budget){
       budgetMapper.insert(budget);
   }

   public void update(Budget budget){
       budgetMapper.update(budget);
   }
}
