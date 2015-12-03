/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.openlmis.core.domain.Money;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MoneyTypeHandler is used for setting corresponding representation of Money object in prepared statement or getting
 * corresponding Money representation of value used in prepared statement. It is used for conversion of custom data type
 * Money into Java data types.
 */

@MappedTypes(Money.class)
public class MoneyTypeHandler implements TypeHandler {


  public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    if (parameter != null) {
      ps.setBigDecimal(i, ((Money) parameter).getValue());
    } else {
      ps.setTimestamp(i, null);
    }
  }

  public Object getResult(ResultSet rs, String columnName) throws SQLException {
    BigDecimal bigDecimal = rs.getBigDecimal(columnName);
    if (bigDecimal != null) {
      return new Money(bigDecimal.toString());
    } else {
      return null;
    }
  }

  @Override
  public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
    BigDecimal bigDecimal = rs.getBigDecimal(columnIndex);
    if (bigDecimal != null) {
      return new Money(bigDecimal.toString());
    } else {
      return null;
    }
  }

  public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
    BigDecimal bigDecimal = cs.getBigDecimal(columnIndex);
    if (bigDecimal != null) {
      return new Money(bigDecimal.toString());
    } else {
      return null;
    }
  }

}
