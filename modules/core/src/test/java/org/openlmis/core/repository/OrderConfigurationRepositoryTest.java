package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.repository.mapper.OrderConfigurationMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderConfigurationRepositoryTest {
  @InjectMocks
  private OrderConfigurationRepository orderConfigurationRepository;

  @Mock
  private OrderConfigurationMapper orderConfigurationMapper;

  @Test
  public void shouldGetConfiguration() {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfiguration.setHeaderInFile(true);
    when(orderConfigurationMapper.get()).thenReturn(orderConfiguration);
    assertThat(orderConfigurationRepository.getConfiguration(), is(orderConfiguration));
    verify(orderConfigurationMapper).get();
  }

  @Test
  public void shouldUpdateConfigurations() throws Exception {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfigurationRepository.update(orderConfiguration);
    verify(orderConfigurationMapper).update(orderConfiguration);
  }
}
