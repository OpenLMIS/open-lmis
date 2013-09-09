package org.openlmis.order.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.springframework.test.context.ContextConfiguration;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
public class OrderRowMapperTest {

  @InjectMocks
  OrderRowMapper orderRowMapper;

  @Mock
  private ResultSet resultSet;

  @Test
  public void shouldCreateOrderFromResultSet() throws Exception {
    when(resultSet.getString("id")).thenReturn("5");
    when(resultSet.getString("rnrid")).thenReturn("4");
    when(resultSet.getString("supplylineid")).thenReturn("55");

    Order order = orderRowMapper.mapRow(resultSet, 1);

    verify(resultSet).getString("id");
    assertThat(order.getId(), is(5L));
    assertThat(order.getRnr().getId(), is(4L));
    assertThat(order.getSupplyLine().getId(), is(55L));
  }
}
