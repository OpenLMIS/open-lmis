package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.repository.BudgetFileRepository;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class BudgetFileServiceTest {

  @Mock
  BudgetFileRepository repository;

  @InjectMocks
  BudgetFileService service;

  @Test
  public void shouldInsertBudgetFileInfoIfDoesNotExist() throws Exception {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("Budget File", false);
    service.save(budgetFileInfo);

    verify(repository).insert(budgetFileInfo);
  }

  @Test
  public void shouldUpdateBudgetFileInfoIfExist() throws Exception {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("Budget File", false);
    budgetFileInfo.setId(1L);
    service.save(budgetFileInfo);

    verify(repository).update(budgetFileInfo);
  }
}
