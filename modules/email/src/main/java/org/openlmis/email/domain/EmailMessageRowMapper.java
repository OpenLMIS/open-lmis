/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.domain;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmailMessageRowMapper implements RowMapper {

  @Override
  public SimpleMailMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
    SimpleMailMessage emailMessage = new SimpleMailMessage();
    emailMessage.setTo(rs.getString("receiver"));
    emailMessage.setSubject(rs.getString("subject"));
    emailMessage.setText(rs.getString("content"));
    return emailMessage;
  }
}
