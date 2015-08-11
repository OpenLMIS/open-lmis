/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.sms.Repository.mapper;


import org.openlmis.sms.domain.SMS;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SMSRowMapper implements RowMapper<SMS> {

  @Override
  public SMS mapRow(ResultSet rs, int rowNum) throws SQLException {
      SMS sms = new SMS();
      sms.setId(rs.getInt("id"));
      sms.setMessage(rs.getString("message"));
      sms.setPhoneNumber(rs.getString("phonenumber"));
      sms.setDirection(rs.getString("direction"));
      sms.setSent(rs.getBoolean("sent"));
      return sms;
  }
}
