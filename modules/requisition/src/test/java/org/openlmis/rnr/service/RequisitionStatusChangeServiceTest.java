package org.openlmis.rnr.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.repository.RequisitionStatusChangeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionStatusChangeServiceTest {

  @Mock
  RequisitionStatusChangeRepository repository;

  @InjectMocks
  RequisitionStatusChangeService service;

  @Test
  public void shouldGetByRnrId() throws Exception {
    List<RequisitionStatusChange> requisitionStatusChanges = new ArrayList<>();
    when(repository.getByRnrId(2L)).thenReturn(requisitionStatusChanges);

    List<RequisitionStatusChange> changes = service.getByRnrId(2L);

    assertThat(changes, is(requisitionStatusChanges));
  }
}
