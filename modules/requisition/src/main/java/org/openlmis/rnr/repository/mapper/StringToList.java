package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for converting a comma-separated-string stored in database to a list.
 */

public class StringToList implements TypeHandler<List> {
  @Override
  public void setParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
  }

  @Override
  public List getResult(ResultSet rs, String columnName) throws SQLException {
    String commaSeparatedString = rs.getString(columnName).replace("[", "").replace("]", "");
    List integers = new ArrayList();
    if (!commaSeparatedString.equals("")) {
      for (String str : commaSeparatedString.split(",")) {
        integers.add(Integer.parseInt(str.trim()));
      }
    }
    return integers;
  }

  @Override
  public List getResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }

  @Override
  public List getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return null;
  }
}
