/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.strategy;

import org.openlmis.rnr.domain.Rnr;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;

/**
 * This class is a abstract class to search for requisitions and acts as base class for other strategies.
 */

public abstract class RequisitionSearchStrategy {

  boolean isSearchable(String rightName) {
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
