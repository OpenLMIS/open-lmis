package org.openlmis.pod.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.mapper.PODMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PODRepositoryTest {

  @Mock
  PODMapper podMapper;

  @InjectMocks
  PODRepository podRepository;

  @Test
  public void shouldInsertPODLineItem() {

    PODLineItem podLineItem = new PODLineItem();
    podRepository.insertPODLineItem(podLineItem);
    verify(podMapper).insertPODLineItem(podLineItem);
  }

  @Test
  public void shouldInsertPOD() {
    POD pod = new POD();
    podRepository.insertPOD(pod);
    verify(podMapper).insertPOD(pod);
  }

  @Test
  public void shouldGetPODByOrderId() {
    Long orderId = 1l;
    POD expectedPOD = new POD();
    when(podMapper.getPODByOrderId(orderId)).thenReturn(expectedPOD);
    POD pod = podRepository.getPODByOrderId(orderId);
    verify(podMapper).getPODByOrderId(orderId);
    assertThat(pod, is(expectedPOD));
  }
}
