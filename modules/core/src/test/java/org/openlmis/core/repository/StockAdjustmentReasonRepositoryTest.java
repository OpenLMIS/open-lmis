package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockAdjustmentReasonRepositoryTest {

  @Mock
  private StockAdjustmentReasonMapper mapper;

  @InjectMocks
  private StockAdjustmentReasonRepository repository;

  List<StockAdjustmentReason> allReasons;
  List<StockAdjustmentReason> positiveReasons;
  List<StockAdjustmentReason> negativeReasons;

  @Before
  public void setUp() throws Exception {
    StockAdjustmentReason positiveReason = new StockAdjustmentReason();
    positiveReason.setName("POSITIVE_REASON");
    positiveReason.setDescription("Positive Reason");
    positiveReason.setAdditive(true);

    StockAdjustmentReason negativeReason = new StockAdjustmentReason();
    negativeReason.setName("NEGATIVE_REASON");
    negativeReason.setDescription("Negative Reason");
    negativeReason.setAdditive(false);

    allReasons = new ArrayList<>();
    positiveReasons = new ArrayList<>();
    negativeReasons = new ArrayList<>();
    allReasons.add(positiveReason);
    allReasons.add(negativeReason);
    positiveReasons.add(positiveReason);
    negativeReasons.add(negativeReason);

  }

  @Test
  public void shouldGetAllAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(null, null);

    verify(mapper).getAll();
    assertEquals(reasons, allReasons);
  }

  @Test
  public void shouldGetProgramSpecificReasons() {
    when(mapper.getAllByProgram(1L)).thenReturn(positiveReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(null, 1L);

    verify(mapper).getAllByProgram(1L);
    assertEquals(reasons, positiveReasons);
  }

  @Test
  public void shouldGetPositiveAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(true, null);

    verify(mapper).getAll();
    assertEquals(reasons, positiveReasons);
  }

  @Test
  public void shouldGetNegativeAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(false, null);

    verify(mapper).getAll();
    assertEquals(reasons, negativeReasons);
  }
}
