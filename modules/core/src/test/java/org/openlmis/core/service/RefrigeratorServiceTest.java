package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.repository.RefrigeratorRepository;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RefrigeratorServiceTest {

  @InjectMocks
  RefrigeratorService service;

  @Mock
  RefrigeratorRepository repository;

  @Test
  public void shouldGetRefrigeratorsForADeliveryZoneAndProgram() throws Exception {
    service.getRefrigeratorsForADeliveryZoneAndProgram(1L, 1L);

    verify(repository).getRefrigeratorsForADeliveryZoneAndProgram(1L, 1L);
  }
}
