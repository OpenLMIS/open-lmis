/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.email.mapper;

import org.openlmis.email.domain.EmailMessage;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * It maps each row of database representation of OpenlmisEmailMessage to corresponding OpenlmisEmailMessage entity.
 */

@Component
public class EmailMessageRowMapper implements RowMapper<EmailMessage> {

  @Override
  public EmailMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setTo(rs.getString("receiver"));
    emailMessage.setSubject(rs.getString("subject"));
    emailMessage.setText(rs.getString("content"));
    emailMessage.setHtml(rs.getBoolean("isHtml"));
    emailMessage.setId(rs.getLong("id"));
    return emailMessage;
  }
}
