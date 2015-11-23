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
  List<StockAdjustmentReason> catReasons;

  @Before
  public void setUp() throws Exception {
    StockAdjustmentReason defaultCatReason = StockAdjustmentReason.create("CATEGORY REASON");
    StockAdjustmentReason positiveReason = StockAdjustmentReason.create("POSITIVE_REASON");
    StockAdjustmentReason negativeReason = StockAdjustmentReason.create("NEGATIVE_REASON");
    negativeReason.setAdditive(false);

    allReasons = new ArrayList<>();
    positiveReasons = new ArrayList<>();
    negativeReasons = new ArrayList<>();
    catReasons = new ArrayList<>();
    allReasons.add(positiveReason);
    allReasons.add(negativeReason);
    allReasons.add(defaultCatReason);
    positiveReasons.add(positiveReason);
    positiveReasons.add(defaultCatReason);
    negativeReasons.add(negativeReason);
    catReasons.add(defaultCatReason);
  }

  @Test
  public void shouldGetAllAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(null, null, null);

    verify(mapper).getAll();
    assertEquals(reasons, allReasons);
  }

  @Test
  public void shouldGetProgramSpecificReasons() {
    when(mapper.getAllByProgram(1L)).thenReturn(positiveReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(null, 1L, null);

    verify(mapper).getAllByProgram(1L);
    assertEquals(reasons, positiveReasons);
  }

  @Test
  public void shouldGetPositiveAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(true, null, null);

    verify(mapper).getAll();
    assertEquals(reasons, positiveReasons);
  }

  @Test
  public void shouldGetNegativeAdjustmentReasons() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(false, null, null);

    verify(mapper).getAll();
    assertEquals(reasons, negativeReasons);
  }

  @Test
  public void shouldGetAdjustmentReasonsInDefaultCategory() {
    when(mapper.getAll()).thenReturn(allReasons);

    List<StockAdjustmentReason> reasons = repository.getAdjustmentReasons(false,
      null,
      StockAdjustmentReason.Category.DEFAULT);

    verify(mapper).getAll();
    assertEquals(reasons, allReasons);
  }
}
