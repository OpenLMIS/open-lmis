package org.openlmis.report.builder;


import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.openlmis.core.hash.Encoder;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({Encoder.class, AdjustmentSummaryQueryBuilder.class})
public class AdjustmentSummaryQueryBuilderTest {

  private AdjustmentSummaryQueryBuilder adjustmentSummaryQueryBuilder;

  @Test
  public void shouldNotThrowErrorWhenEmptyFilterIsPassed() {
    String query = AdjustmentSummaryQueryBuilder.getData(null);
  }

  @Test
  public void shouldNotThrowErrorWhenEmptyMapIsPassed() {
    String query = AdjustmentSummaryQueryBuilder.getData(null);
  }
}
