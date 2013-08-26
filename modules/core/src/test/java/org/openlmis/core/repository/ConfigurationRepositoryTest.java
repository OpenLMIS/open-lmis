package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.repository.mapper.ConfigurationMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationRepositoryTest {
  @InjectMocks
  private ConfigurationRepository configurationRepository;

  @Mock
  private ConfigurationMapper configurationMapper;

  @Test
  public void shouldGetConfiguration() {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfiguration.setHeaderInFile(true);
    when(configurationMapper.getConfiguration()).thenReturn(orderConfiguration);
    assertThat(configurationRepository.getConfiguration(), is(orderConfiguration));
    verify(configurationMapper).getConfiguration();
  }

  @Test
  public void shouldUpdateConfigurations() throws Exception {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    configurationRepository.update(orderConfiguration);
    verify(configurationMapper).update(orderConfiguration);
  }
}
