/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.ShipmentFileDetail;
import org.openlmis.core.repository.BudgetRepository;
import org.openlmis.core.repository.ShipmentFileDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentFileDetailService {

  @Autowired
  ShipmentFileDetailRepository repository;

  public void save(ShipmentFileDetail detail) {
    if (detail.getId() != null)
      repository.update(detail);
    else
      repository.insert(detail);
  }

  public ShipmentFileDetail getByCodes(String orderId, String productCode) {
    return repository.getByReferenceCodes(orderId, productCode);
  }


}
