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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
public class QueryExecutor {

    DataSource dataSource;

    @Autowired
    public QueryExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ResultSet query(String query) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    public int insertOrUpdate(String insertQuery, List params) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        for (int index = 0; index < params.size(); index++){
            preparedStatement.setObject(index+1, params.get(index));
        }
        return preparedStatement.executeUpdate();
    }
}
