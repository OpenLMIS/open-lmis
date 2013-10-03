/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.domain;

import org.openlmis.core.domain.SupplyLine;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OrderRowMapper implements RowMapper {
  @Override
  public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Order(Long.parseLong(rs.getString("id")), new Rnr(Long.parseLong(rs.getString("rnrid"))),
      new SupplyLine(Long.parseLong(rs.getString("supplylineid"))));
  }
}
