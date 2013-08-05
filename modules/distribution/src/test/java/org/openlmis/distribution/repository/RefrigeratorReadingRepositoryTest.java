package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.RefrigeratorReadingMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RefrigeratorReadingRepositoryTest {

  @InjectMocks
  RefrigeratorReadingRepository refrigeratorReadingRepository;

  @Mock
  RefrigeratorReadingMapper refrigeratorReadingMapper;

  @Test
  public void shouldGetRefrigeratorReading() throws Exception {

    RefrigeratorReading reading = new RefrigeratorReading();
    when(refrigeratorReadingMapper.getByDistribution(1L, 1L)).thenReturn(reading);

    RefrigeratorReading dbRefrigeratorReading = refrigeratorReadingRepository.getByDistribution(1L, 1L);

    verify(refrigeratorReadingMapper).getByDistribution(1L, 1L);

    assertThat(dbRefrigeratorReading,is(reading));
  }
}
