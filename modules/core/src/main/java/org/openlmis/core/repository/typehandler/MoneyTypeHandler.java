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
