package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.repository.RegimenColumnRepository;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.db.categories.UnitTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class RegimenColumnServiceTest {

  @Mock
  RegimenColumnRepository repository;

  RegimenColumnService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new RegimenColumnService(repository);
  }

  @Test
  public void shouldCallUpdateWhenRegimenColumnHasId() throws Exception {

    RegimenColumn regimenColumn = new RegimenColumn("testName", "testLabel", true, "numeric", 1L);

    service.save(regimenColumn);

    verify(repository).insert(regimenColumn);
  }

  @Test
  public void shouldCallInsertWhenRegimenColumnDoesNotHaveId() throws Exception {
    RegimenColumn regimenColumn = new RegimenColumn("testName", "testLabel", true, "numeric", 1L);
    regimenColumn.setId(1L);

    service.save(regimenColumn);

    verify(repository).update(regimenColumn);
  }

  @Test
  public void shouldSaveRegimenColumnList() throws Exception {

    RegimenColumn regimenColumn1 = new RegimenColumn("testName1", "testLabel1", true, "numeric", 1L);
    RegimenColumn regimenColumn2 = new RegimenColumn("testName2", "testLabel2", true, "numeric", 1L);
    regimenColumn2.setId(1L);

    service.save(Arrays.asList(regimenColumn1, regimenColumn2));

    verify(repository).insert(regimenColumn1);
    verify(repository).update(regimenColumn2);
  }
}
