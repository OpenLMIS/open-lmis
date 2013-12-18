package org.openlmis.distribution.domain;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RefrigeratorReadingTest {

  @Test
  public void shouldCreateRefrigeratorReadingForARefrigerator() throws Exception {
    Refrigerator refrigerator = new Refrigerator("LG", "S. No.", "Model", 4L);
    refrigerator.setId(16L);

    RefrigeratorReading refrigeratorReading = new RefrigeratorReading(refrigerator);

    assertThat(refrigeratorReading.getBrand(), is("LG"));
    assertThat(refrigeratorReading.getSerialNumber(), is("S. No."));
    assertThat(refrigeratorReading.getModel(), is("Model"));
    assertThat(refrigeratorReading.getRefrigeratorId(), is(16L));
  }
}
