package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EpiUseTest {

  @Test
  public void shouldNotGetProductGroupForAllInactiveProducts() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct2 = mock(FacilityProgramProduct.class);

    when(facilityProgramProduct1.getActiveProductGroup()).thenReturn(new ProductGroup("PG1", "PG1"));
    when(facilityProgramProduct2.getActiveProductGroup()).thenReturn(null);

    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    FacilityVisit facilityVisit = new FacilityVisit();

    EpiUse epiUse = new EpiUse(facility, facilityVisit);

    List<EpiUseLineItem> lineItems = epiUse.getLineItems();
    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getProductGroup().getCode(), is("PG1"));
    assertThat(lineItems.get(0).getProductGroup().getName(), is("PG1"));
  }

}
