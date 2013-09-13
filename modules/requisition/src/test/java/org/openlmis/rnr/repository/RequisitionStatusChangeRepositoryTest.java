package org.openlmis.rnr.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.repository.mapper.RequisitionStatusChangeMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class RequisitionStatusChangeRepositoryTest {

  @InjectMocks
  RequisitionStatusChangeRepository repository;

  @Mock
  RequisitionStatusChangeMapper mapper;

  @Test
  public void shouldGetByRnrId() throws Exception {
    List<RequisitionStatusChange> requisitionStatusChanges = new ArrayList<>();
    when(mapper.getByRnrId(2L)).thenReturn(requisitionStatusChanges);

    List<RequisitionStatusChange> changes = repository.getByRnrId(2L);

    assertThat(changes, is(requisitionStatusChanges));
  }
}
