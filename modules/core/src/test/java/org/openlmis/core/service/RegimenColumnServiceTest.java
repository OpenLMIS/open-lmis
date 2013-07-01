package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.repository.RegimenColumnRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class RegimenColumnServiceTest {

  @Mock
  RegimenColumnRepository repository;

  @Mock
  MessageService messageService;

  RegimenColumnService service;

  @Before
  public void setUp() throws Exception {
    service = new RegimenColumnService(repository, messageService);
  }

  @Test
  public void shouldCallUpdateWhenRegimenColumnHasId() throws Exception {

    RegimenColumn regimenColumn = new RegimenColumn(1L, "testName", "testLabel", "numeric", true);

    service.save(regimenColumn);

    verify(repository).insert(regimenColumn);
  }

  @Test
  public void shouldCallInsertWhenRegimenColumnDoesNotHaveId() throws Exception {
    RegimenColumn regimenColumn = new RegimenColumn(1L, "testName", "testLabel", "numeric", true);
    regimenColumn.setId(1L);

    service.save(regimenColumn);

    verify(repository).update(regimenColumn);
  }

  @Test
  public void shouldSaveRegimenColumnList() throws Exception {

    RegimenColumn regimenColumn1 = new RegimenColumn(1L, "testName1", "testLabel1", "numeric", true);
    RegimenColumn regimenColumn2 = new RegimenColumn(1L, "testName2", "testLabel2", "numeric", true);
    regimenColumn2.setId(1L);

    service.save(Arrays.asList(regimenColumn1, regimenColumn2));

    verify(repository).insert(regimenColumn1);
    verify(repository).update(regimenColumn2);
  }

  @Test
  public void shouldGetDefaultRegimenColumnsByProgramIdWhenNotPresentAlready() throws Exception {

    Long programId = 1L;
    List<RegimenColumn> emptyList = new ArrayList<>();
    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(emptyList);

    service.getRegimenColumnsByProgramId(programId);

    verify(repository).insert(new RegimenColumn(programId, "onTreatment", null, null, true));
    verify(repository).insert(new RegimenColumn(programId, "initiatedTreatment", null, null, true));
    verify(repository).insert(new RegimenColumn(programId, "stoppedTreatment", null, null, true));
    verify(repository).insert(new RegimenColumn(programId, "remarks", null, null, true));
    verify(repository, times(2)).getRegimenColumnsByProgramId(programId);
  }

  @Test
  public void shouldGetRegimenColumnsByProgramId() throws Exception {
    Long programId = 1L;
    RegimenColumn regimenColumn1 = new RegimenColumn(programId, "testName1", "testLabel1", "numeric", true);
    RegimenColumn regimenColumn2 = new RegimenColumn(programId, "testName2", "testLabel2", "numeric", true);

    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(Arrays.asList(regimenColumn1, regimenColumn2));

    List<RegimenColumn> resultColumns = service.getRegimenColumnsByProgramId(programId);

    verify(repository).getRegimenColumnsByProgramId(programId);
    assertThat(resultColumns.size(), is(2));
    assertThat(resultColumns.get(0), is(regimenColumn1));
    assertThat(resultColumns.get(1), is(regimenColumn2));
  }

}
