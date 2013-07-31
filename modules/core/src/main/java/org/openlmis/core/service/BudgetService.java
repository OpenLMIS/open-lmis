/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
