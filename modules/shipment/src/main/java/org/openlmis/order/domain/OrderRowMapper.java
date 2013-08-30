package org.openlmis.order.domain;

import org.openlmis.rnr.domain.Rnr;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OrderRowMapper implements RowMapper {
  @Override
  public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Order(Long.parseLong(rs.getString("id")), new Rnr(Long.parseLong(rs.getString("rnrid"))));
  }
}
