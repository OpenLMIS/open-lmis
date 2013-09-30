/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.openlmis.core.domain.Right;
import org.openlmis.rnr.domain.Rnr;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

public abstract class RequisitionSearchStrategy {

  boolean isSearchable(Right right) {
    return true;
  }

  public List<Rnr> search() {
    if (isSearchable(VIEW_REQUISITION)) {
      return findRequisitions();
    }

    return emptyList();
  }

  abstract List<Rnr> findRequisitions();
}
