package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(FacilityProgramProduct.class)
public class EpiUseTest {

  @Test
  public void shouldNotGetProductGroupForAllInactiveProducts() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct2 = mock(FacilityProgramProduct.class);

    when(facilityProgramProduct1.getActiveProductGroup()).thenReturn(new ProductGroup("PG1", "PG1"));
    when(facilityProgramProduct2.getActiveProductGroup()).thenReturn(null);

    List<FacilityProgramProduct> programProducts = asList(facilityProgramProduct1, facilityProgramProduct2);
    programSupported.setProgramProducts(programProducts);
    facility.setSupportedPrograms(asList(programSupported));

    mockStatic(FacilityProgramProduct.class);
    when(FacilityProgramProduct.filterActiveProducts(programProducts)).thenReturn(programProducts);
    FacilityVisit facilityVisit = new FacilityVisit();

    EpiUse epiUse = new EpiUse(facility, facilityVisit);

    List<EpiUseLineItem> lineItems = epiUse.getLineItems();
    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getProductGroup().getCode(), is("PG1"));
    assertThat(lineItems.get(0).getProductGroup().getName(), is("PG1"));
  }

  @Test
  public void shouldSortLineItemsByProductGroupCode() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct2 = mock(FacilityProgramProduct.class);
    FacilityProgramProduct facilityProgramProduct3 = mock(FacilityProgramProduct.class);

    ProductGroup productGroup1 = new ProductGroup("PG3", "PG3");
    ProductGroup productGroup2 = new ProductGroup("PG1", "PG1");

    when(facilityProgramProduct1.getActiveProductGroup()).thenReturn(productGroup1);
    when(facilityProgramProduct2.getActiveProductGroup()).thenReturn(null);
    when(facilityProgramProduct3.getActiveProductGroup()).thenReturn(productGroup2);

    List<FacilityProgramProduct> programProducts = asList(facilityProgramProduct1, facilityProgramProduct2, facilityProgramProduct3);
    programSupported.setProgramProducts(programProducts);
    facility.setSupportedPrograms(asList(programSupported));

    mockStatic(FacilityProgramProduct.class);
    when(FacilityProgramProduct.filterActiveProducts(programProducts)).thenReturn(programProducts);
    FacilityVisit facilityVisit = new FacilityVisit();

    EpiUse epiUse = new EpiUse(facility, facilityVisit);

    List<EpiUseLineItem> lineItems = epiUse.getLineItems();
    assertThat(lineItems.size(), is(2));
    assertThat(lineItems.get(0).getProductGroup().getCode(), is(productGroup2.getCode()));
    assertThat(lineItems.get(0).getProductGroup().getName(), is(productGroup2.getName()));
    assertThat(lineItems.get(1).getProductGroup().getCode(), is(productGroup1.getCode()));
    assertThat(lineItems.get(1).getProductGroup().getName(), is(productGroup1.getName()));
  }

}
