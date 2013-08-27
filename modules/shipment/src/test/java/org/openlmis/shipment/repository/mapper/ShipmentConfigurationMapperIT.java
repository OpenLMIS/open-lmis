package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ShipmentConfigurationMapperIT {


  @Autowired
  ShipmentConfigurationMapper mapper;

  @Test
  public void shouldGetShipmentConfiguration() {
    ShipmentConfiguration shipmentConfiguration = mapper.get();
    assertThat(shipmentConfiguration.isHeaderInFile(), is(false));
  }

  @Test
  public void shouldUpdateShipmentConfiguration() {
    ShipmentConfiguration shipmentConfiguration = mapper.get();
    shipmentConfiguration.setHeaderInFile(true);
    mapper.update(shipmentConfiguration);
    shipmentConfiguration = mapper.get();
    assertThat(shipmentConfiguration.isHeaderInFile(), is(true));
  }
}



