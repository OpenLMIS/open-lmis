package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingPeriodRepositoryTest {

  ProcessingPeriodRepository repository;

  @Mock
  ProcessingPeriodMapper mapper;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new ProcessingPeriodRepository(mapper);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> processingPeriodList = new ArrayList<>();
    when(mapper.getAll(123)).thenReturn(processingPeriodList);
    List<ProcessingPeriod> periods = repository.getAll(123);

    verify(mapper).getAll(123);
    assertThat(periods, is(processingPeriodList));
  }
}
