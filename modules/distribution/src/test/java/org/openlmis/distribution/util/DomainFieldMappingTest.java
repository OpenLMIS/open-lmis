package org.openlmis.distribution.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.dto.AdultCoverageLineItemDTO;
import org.openlmis.distribution.dto.ChildCoverageLineItemDTO;
import org.openlmis.distribution.dto.EpiInventoryLineItemDTO;
import org.openlmis.distribution.dto.EpiUseLineItemDTO;
import org.openlmis.distribution.dto.FacilitatorDTO;
import org.openlmis.distribution.dto.FacilityVisitDTO;
import org.openlmis.distribution.dto.OpenedVialLineItemDTO;
import org.openlmis.distribution.dto.RefrigeratorProblemDTO;
import org.openlmis.distribution.dto.RefrigeratorReadingDTO;
import org.openlmis.distribution.dto.VaccinationFullCoverageDTO;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.distribution.util.DomainFieldMapping.fieldMapping;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DomainFieldMappingTest {

  @Test
  public void shouldReturnCorrectDtoFieldNameForDomainFieldName() throws Exception {
    assertThat(fieldMapping(RefrigeratorReadingDTO.class, "problem"), is("problems"));

    assertThat(fieldMapping(VaccinationFullCoverageDTO.class, "femaleHealthCenter"), is("femaleHealthCenterReading"));
    assertThat(fieldMapping(VaccinationFullCoverageDTO.class, "femaleOutreach"), is("femaleMobileBrigadeReading"));
    assertThat(fieldMapping(VaccinationFullCoverageDTO.class, "maleHealthCenter"), is("maleHealthCenterReading"));
    assertThat(fieldMapping(VaccinationFullCoverageDTO.class, "maleOutreach"), is("maleMobileBrigadeReading"));

    assertThat(fieldMapping(OpenedVialLineItemDTO.class, "openedVials"), is("openedVial"));
  }

  @Test
  public void shouldReturnPassedPropertyNameIfNoMapping() throws Exception {
    String propertyName = "<<property_name>>";

    assertThat(fieldMapping(AdultCoverageLineItemDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(ChildCoverageLineItemDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(EpiInventoryLineItemDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(EpiUseLineItemDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(FacilitatorDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(FacilityVisitDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(RefrigeratorReadingDTO.class, propertyName), is(propertyName));
    assertThat(fieldMapping(RefrigeratorProblemDTO.class, propertyName), is(propertyName));
  }
}
