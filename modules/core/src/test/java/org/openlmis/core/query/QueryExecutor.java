/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.query;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
@NoArgsConstructor
public class QueryExecutor {

  DataSource dataSource;

  @Autowired
  public QueryExecutor(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ResultSet execute(String query) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    return preparedStatement.executeQuery();
  }

  //TODO release connection
  public ResultSet execute(String query, List params) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    for (int index = 0; index < params.size(); index++) {
      preparedStatement.setObject(index + 1, params.get(index));
    }
    return preparedStatement.executeQuery();
  }

  public long executeUpdate(String query, List params) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      for (int index = 0; index < params.size(); index++) {
        preparedStatement.setObject(index + 1, params.get(index));
      }
      preparedStatement.executeUpdate();

      ResultSet rs = preparedStatement.getGeneratedKeys();
      long id = -1;
      if (rs.next()) {
        id = rs.getInt(1);
      }
      return id;
    }
  }
}
