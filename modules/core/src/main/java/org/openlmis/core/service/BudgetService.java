/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.repository.BudgetRepository;
import org.openlmis.core.repository.DeliveryZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {

  @Autowired
  BudgetRepository repository;



  public void save(Budget budget) {
    // set the reference dat


    if (budget.getId() != null)
      repository.update(budget);
    else
      repository.insert(budget);
  }

  public Budget getByCodes(String programCode, String periodName, String facilityCode) {
    return repository.getByReferenceCodes(programCode, periodName, facilityCode);
  }


}
