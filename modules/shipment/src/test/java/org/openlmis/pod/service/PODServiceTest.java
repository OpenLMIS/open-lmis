package org.openlmis.pod.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.PODRepository;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PODServiceTest {
  @Mock
  private PODRepository podRepository;

  @InjectMocks
  private PODService podService;

  @Test
  public void shouldUpdatePOD() {
    Long podId = 1l;
    POD pod = new POD();
    pod.setId(podId);
    List<PODLineItem> lineItems = asList(new PODLineItem(podId, "productCode1", 100), new PODLineItem(podId, "productCode2", 100));
    pod.setPodLineItems(lineItems);
    podService.updatePOD(pod);
    verify(podRepository).insertPOD(pod);
    verify(podRepository, times(2)).insertPODLineItem(any(PODLineItem.class));
  }

  @Test
  public void shouldGetPODByOrderId(){
    Long orderId = 1l;
    POD expectedPOD = new POD();
    when(podRepository.getPODByOrderId(orderId)).thenReturn(expectedPOD);
    POD pod = podService.getPODByOrderId(orderId);
    verify(podRepository).getPODByOrderId(orderId);
    assertThat(pod, is(expectedPOD));
  }

}
