package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ShipmentFileColumnMapperIT {

  @Autowired
  private ShipmentFileColumnMapper mapper;

  @Test
  public void shouldInsertShipmentFileColumn() {
    List<ShipmentFileColumn> existingColumns = mapper.getAll();
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn("Order Number", 1, true, true, null);

    mapper.insert(shipmentFileColumn);

    List<ShipmentFileColumn> updatedColumns = mapper.getAll();
    assertThat(existingColumns.size() + 1, is(updatedColumns.size()));
  }

  @Test
  public void shouldGetAllShipmentFileColumns() {
    assertThat(mapper.getAll().size(), is(6));
  }

  @Test
  public void shouldDeleteAllShipmentFileColumns() {
    mapper.deleteAll();
    assertThat(mapper.getAll().size(), is(0));
  }
}