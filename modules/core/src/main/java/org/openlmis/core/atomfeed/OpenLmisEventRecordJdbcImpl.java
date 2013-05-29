/**
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.atomfeed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.repository.jdbc.JdbcResultSetMapper;
import org.springframework.context.annotation.Primary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class OpenLmisEventRecordJdbcImpl extends AllEventRecordsJdbcImpl {

  private JdbcConnectionProvider provider;
  private String schema = "atomfeed";
  private String category;


  public OpenLmisEventRecordJdbcImpl(JdbcConnectionProvider provider) {
    super(provider);
    this.provider = provider;
  }

  private Connection getDbConnection() throws SQLException {
    return provider.getConnection();
  }

  private List<EventRecord> mapEventRecords(ResultSet results) {
    return new JdbcResultSetMapper<EventRecord>().mapResultSetToObject(results, EventRecord.class);
  }

  private void closeAll(PreparedStatement stmt, ResultSet rs) {
    try {
      if (rs != null) {
        rs.close();
      }
      if (stmt != null) {
        stmt.close();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String getTableName(String table) {
    if ((schema != null) && (!"".equals(schema))) {
      return schema + "." + table;
    } else {
      return table;
    }
  }

  @Override
  public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
    Connection connection = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = null;
    try {
      connection = getDbConnection();
      if (category != null) {
        sql = String.format("select id, uuid, title, timestamp, uri, object from %s where id >= ? and id <= ? and LOWER(category)=LOWER(?)", getTableName("event_records"));
      } else {
        sql = String.format("select id, uuid, title, timestamp, uri, object from %s where id >= ? and id <= ?", getTableName("event_records"));
      }
      stmt = connection.prepareStatement(sql);
      stmt.setInt(1, first);
      stmt.setInt(2, last);
      if (category != null) stmt.setString(3, category);

      ResultSet results = stmt.executeQuery();
      return mapEventRecords(results);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      closeAll(stmt, rs);
    }
  }

}
