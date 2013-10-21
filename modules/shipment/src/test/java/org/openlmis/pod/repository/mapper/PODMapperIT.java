package org.openlmis.pod.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.context.ApplicationTestContext;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.pod.domain.PODLineItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
public class PODMapperIT extends ApplicationTestContext {

  @Autowired
  PODMapper podMapper;

  @Autowired
  QueryExecutor queryExecutor;

  String productCode;
  Order order;

  @Before
  public void setUp() throws Exception {

    productCode = "P10";
    order = insertOrder(productCode);
  }

  @Test
  public void shouldInsertPODLineItem() {

    PODLineItem podLineItem = new PODLineItem(order.getId(), productCode, 100);
    podMapper.insert(podLineItem);

    List<PODLineItem> podLineItems = podMapper.getPODLineItemsByOrderId(order.getId());
    assertThat(podLineItems.size(), CoreMatchers.is(1));
    assertThat(podLineItems.get(0).getProductCode(), CoreMatchers.is(productCode));
  }


  @Test
  public void shouldGetPodLineItemsByOrderId() throws SQLException {

  //  queryExecutor.execute("INSERT INTO pod_line_items (orderId, productCode, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?)", )

  }


}
