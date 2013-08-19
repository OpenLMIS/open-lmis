package org.openlmis.core.repository.mapper;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Configuration;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ConfigurationMapperIT {
  @Autowired
  private ConfigurationMapper mapper;

  @Test
  public void shouldGetConfiguration() throws Exception {
    Configuration configuration = mapper.getConfiguration();
    assertThat(configuration.getOrderFilePrefix(), is("O"));
    assertThat(configuration.headerInOrderFile(), is(false));
    assertThat(configuration.getOrderDatePattern(), is("ddMMyyyy"));
    assertThat(configuration.getPeriodDatePattern(), is("MMyyyy"));
  }
}
