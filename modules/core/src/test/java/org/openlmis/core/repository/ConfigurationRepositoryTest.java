package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Configuration;
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
    Configuration configuration = new Configuration();
    configuration.setHeaderInOrderFile(true);
    configuration.setOrderDatePattern("ddMMyy");
    when(configurationMapper.getConfiguration()).thenReturn(configuration);
    assertThat(configurationRepository.getConfiguration(), is(configuration));
    verify(configurationMapper).getConfiguration();
  }

  @Test
  public void shouldUpdateConfigurations() throws Exception {
    Configuration configuration = new Configuration();
    configurationRepository.update(configuration);
    verify(configurationMapper).update(configuration);
  }
}
