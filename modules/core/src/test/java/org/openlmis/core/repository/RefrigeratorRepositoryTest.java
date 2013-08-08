package org.openlmis.core.repository;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.repository.mapper.RefrigeratorMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RefrigeratorRepositoryTest {

  @Mock
  RefrigeratorMapper mapper;

  @InjectMocks
  RefrigeratorRepository repository;

  @Test
  public void shouldGetRefrigeratorsForDeliveryZoneAndProgram() throws Exception {

    Long deliveryZoneId = 1L;
    Long programId = 1L;
    List<Refrigerator> refrigerators = new ArrayList<>();
    when(mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId)).thenReturn(refrigerators);

    List<Refrigerator> resultRefrigerators = repository.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);

    assertThat(resultRefrigerators, is(refrigerators));
    verify(mapper).getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
  }

}
