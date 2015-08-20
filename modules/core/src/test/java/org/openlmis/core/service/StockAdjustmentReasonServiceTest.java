package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StockAdjustmentReasonServiceTest {

  @Mock
  ProgramService programService;

  @Mock
  StockAdjustmentReasonRepository repository;

  @InjectMocks
  StockAdjustmentReasonService service;

  private StockAdjustmentReason reason;
  private StockAdjustmentReason newReason;
  private StockAdjustmentReasonProgram reasonProgram;
  private StockAdjustmentReasonProgram newReasonProgram;

  @Before
  public void setUp() throws Exception {
    // Assume that there is a program in the system
    Program program = programService.getAll().get(0);

    reason = new StockAdjustmentReason();
    reason.setName("TEST_REASON");
    reason.setDescription("Test Reason");

    newReason = new StockAdjustmentReason();
    newReason.setName("NEW_REASON");
    newReason.setDescription("New Reason");

    service.saveAdjustmentReason(reason);

    reasonProgram = new StockAdjustmentReasonProgram();
    reasonProgram.setReason(reason);
    reasonProgram.setProgram(program);

    newReasonProgram = new StockAdjustmentReasonProgram();
    newReasonProgram.setReason(newReason);
    newReasonProgram.setProgram(program);

    service.saveAdjustmentReasonProgram(reasonProgram);
  }

  @Test
  public void shouldUpdateExistingAdjustmentReason() throws Exception {
    reason.setDescription("Changed Reason");

    service.saveAdjustmentReason(reason);

    verify(repository).updateAdjustmentReason(reason);
    verify(repository, never()).insertAdjustmentReason(reason);
  }

  @Test
  public void shouldInsertNewAdjustmentReason() throws Exception {
    service.saveAdjustmentReason(newReason);

    verify(repository).insertAdjustmentReason(newReason);
    verify(repository, never()).updateAdjustmentReason(newReason);
  }

  @Test
  public void shouldUpdateExistingAdjustmentReasonProgram() throws Exception {
    service.saveAdjustmentReasonProgram(reasonProgram);

    verify(repository).updateAdjustmentReasonProgram(reasonProgram);
    verify(repository, never()).insertAdjustmentReasonProgram(reasonProgram);
  }

  @Test
  public void shouldInsertNewAdjustmentReasonProgram() throws Exception {
    service.saveAdjustmentReasonProgram(newReasonProgram);

    verify(repository).insertAdjustmentReasonProgram(newReasonProgram);
    verify(repository, never()).updateAdjustmentReasonProgram(newReasonProgram);
  }
}
