package org.openlmis.shipment.repository.mapper;

import org.junit.Before;
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

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.columnPosition;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.mandatoryShipmentFileColumn;
import static org.testng.Assert.assertNull;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ShipmentFileColumnMapperIT {

  @Autowired
  private ShipmentFileColumnMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper.deleteAll();
  }

  @Test
  public void shouldInsertShipmentFileColumn() {

    ShipmentFileColumn shipmentFileColumn = make(a(mandatoryShipmentFileColumn, with(columnPosition, 10)));

    mapper.insert(shipmentFileColumn);

    List<ShipmentFileColumn> updatedColumns = mapper.getAll();
    assertThat(updatedColumns.size(), is(1));
    assertThat(updatedColumns.get(0).getName(), is("name"));
    assertThat(updatedColumns.get(0).getDataFieldLabel(), is("label"));
    assertNull(updatedColumns.get(0).getDatePattern());
    assertThat(updatedColumns.get(0).getInclude(), is(true));
    assertThat(updatedColumns.get(0).getMandatory(), is(true));
    assertThat(updatedColumns.get(0).getPosition(), is(10));
  }

  @Test
  public void shouldGetAllShipmentFileColumns() {
    assertThat(mapper.getAll().size(), is(0));

    ShipmentFileColumn shipmentFileColumn = make(a(mandatoryShipmentFileColumn, with(columnPosition, 10)));
    mapper.insert(shipmentFileColumn);

    assertThat(mapper.getAll().size(), is(1));

  }

  @Test
  public void shouldDeleteAllShipmentFileColumns() {
    ShipmentFileColumn shipmentFileColumn1 = make(a(mandatoryShipmentFileColumn, with(columnPosition, 10)));
    mapper.insert(shipmentFileColumn1);

    ShipmentFileColumn shipmentFileColumn2 = make(a(mandatoryShipmentFileColumn, with(columnPosition, 20)));
    mapper.insert(shipmentFileColumn2);

    assertThat(mapper.getAll().size(), is(2));

    mapper.deleteAll();
    assertThat(mapper.getAll().size(), is(0));
  }
}
