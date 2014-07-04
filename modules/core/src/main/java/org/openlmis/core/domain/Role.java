/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.List;

import static com.google.common.collect.Iterables.any;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.core.utils.RightUtil.contains;
import static org.openlmis.core.utils.RightUtil.with;

/**
 * Role represents Role entity which is a set of rights. Also provides methods to validate if a role contains related rights.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseModel {
  private String name;
  private String description;
  private List<Right> rights;

  public Role(String name, String description) {
    this(name, description, null);
  }

  public void validate() {
    if (isBlank(name)) throw new DataException("error.role.without.name");
    if (rights == null || rights.isEmpty())
      throw new DataException("error.role.without.rights");
    validateForRelatedRights();
  }

  private void validateForRelatedRights() {
    if (any(rights, contains(asList(CREATE_REQUISITION, APPROVE_REQUISITION, AUTHORIZE_REQUISITION)))
      && (!any(rights, with(VIEW_REQUISITION)))) {
      throw new DataException("error.role.related.right.not.selected");
    }
    if (any(rights, contains(asList(CONVERT_TO_ORDER, MANAGE_POD, FACILITY_FILL_SHIPMENT)))
      && (!any(rights,with(VIEW_ORDER)))) {
      throw new DataException("error.role.related.right.not.selected");
    }
  }
}
