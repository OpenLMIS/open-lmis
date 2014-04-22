/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.query;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

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

  public ResultSet execute(String query, Object... params) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    for (int index = 0; index < params.length; index++) {
      preparedStatement.setObject(index + 1, params[index]);
    }
    return preparedStatement.executeQuery();
  }

  public long executeUpdate(String query, Object... params) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      for (int index = 0; index < params.length; index++) {
        preparedStatement.setObject(index + 1, params[index]);
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

  public void executeQuery(String query) throws SQLException {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    Statement statement = connection.createStatement();
    statement.execute(query);
  }
}
