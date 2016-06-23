package org.openlmis.web.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.EpiInventory;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.Facilitator;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.domain.VaccinationAdultCoverage;
import org.openlmis.distribution.domain.VaccinationChildCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.dto.FacilityDistributionDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class FacilityDistributionEditHandlerTest {

  private FacilityDistribution original;
  private FacilityDistributionDTO replacement;
  private FacilityDistributionEditHandler handler;

  @Before
  public void setUp() throws Exception {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(1L);
    facilityVisit.setConfirmedBy(new Facilitator("name", "title"));
    facilityVisit.setVerifiedBy(new Facilitator("name", "title"));
    facilityVisit.setVisitDate(new Date());
    facilityVisit.setVisited(true);
    facilityVisit.setObservations("some observations");

    EpiUseLineItem line1 = new EpiUseLineItem();
    line1.setId(2L);
    line1.setProductGroup(new ProductGroup(null, "PG1", null));
    line1.setStockAtFirstOfMonth(10);
    line1.setStockAtEndOfMonth(3);
    line1.setReceived(3);
    line1.setLoss(1);
    line1.setDistributed(3);
    line1.setExpirationDate("06/2016");

    EpiUseLineItem line2 = new EpiUseLineItem();
    line2.setId(3L);
    line2.setProductGroup(new ProductGroup(null, "PG2", null));
    line2.setStockAtFirstOfMonth(10);
    line2.setStockAtEndOfMonth(9);
    line2.setReceived(1);
    line2.setLoss(0);
    line2.setDistributed(0);
    line2.setExpirationDate("06/2016");

    EpiUse epiUse = new EpiUse();
    epiUse.setLineItems(Arrays.asList(line1, line2));

    Refrigerator refrigerator = new Refrigerator("123456789");
    refrigerator.setId(4L);

    RefrigeratorProblem problem = new RefrigeratorProblem(5L, true, true, true, false, false, true, "some other problem");
    problem.setReadingId(5L);

    RefrigeratorReading reading = new RefrigeratorReading();
    reading.setId(6L);
    reading.setRefrigerator(refrigerator);
    reading.setTemperature(8f);
    reading.setFunctioningCorrectly("N");
    reading.setLowAlarmEvents(null);
    reading.setHighAlarmEvents(null);
    reading.setProblemSinceLastTime("N");
    reading.setProblem(problem);

    DistributionRefrigerators refrigerators = new DistributionRefrigerators();
    refrigerators.setId(7L);
    refrigerators.setReadings(Collections.singletonList(reading));

    EpiInventoryLineItem line3 = new EpiInventoryLineItem();
    line3.setId(8L);
    line3.setProductName("PN1");
    line3.setExistingQuantity(10);
    line3.setSpoiledQuantity(0);
    line3.setDeliveredQuantity(10);

    EpiInventoryLineItem line4 = new EpiInventoryLineItem();
    line4.setId(9L);
    line4.setProductName("PN2");
    line4.setExistingQuantity(10);
    line4.setSpoiledQuantity(5);
    line4.setDeliveredQuantity(5);

    EpiInventory epiInventory = new EpiInventory();
    epiInventory.setLineItems(Arrays.asList(line3, line4));

    VaccinationFullCoverage fullCoverage = new VaccinationFullCoverage();
    fullCoverage.setId(10L);
    fullCoverage.setFemaleHealthCenter(10);
    fullCoverage.setFemaleOutreach(5);
    fullCoverage.setMaleHealthCenter(7);
    fullCoverage.setMaleOutreach(2);

    OpenedVialLineItem line5 = new OpenedVialLineItem();
    line5.setId(11L);
    line5.setProductVialName("PVN1");
    line5.setOpenedVials(1);

    ChildCoverageLineItem line6 = new ChildCoverageLineItem();
    line6.setId(12L);
    line6.setVaccination("V1");
    line6.setHealthCenter11Months(11);
    line6.setOutreach11Months(11);
    line6.setHealthCenter23Months(23);
    line6.setOutreach23Months(23);

    ChildCoverageLineItem line7 = new ChildCoverageLineItem();
    line7.setId(13L);
    line7.setVaccination("V2");
    line7.setHealthCenter11Months(23);
    line7.setOutreach11Months(23);
    line7.setHealthCenter23Months(11);
    line7.setOutreach23Months(11);

    VaccinationChildCoverage childCoverage = new VaccinationChildCoverage();
    childCoverage.setChildCoverageLineItems(Arrays.asList(line6, line7));
    childCoverage.setOpenedVialLineItems(Collections.singletonList(line5));

    OpenedVialLineItem line8 = new OpenedVialLineItem();
    line8.setId(14L);
    line8.setProductVialName("PVN2");
    line8.setOpenedVials(3);

    AdultCoverageLineItem line9 = new AdultCoverageLineItem();
    line9.setId(15L);
    line9.setDemographicGroup("DG1");
    line9.setHealthCenterTetanus1(1);
    line9.setOutreachTetanus1(1);
    line9.setHealthCenterTetanus2To5(25);
    line9.setOutreachTetanus2To5(25);

    AdultCoverageLineItem line10 = new AdultCoverageLineItem();
    line10.setId(16L);
    line10.setDemographicGroup("DG2");
    line10.setHealthCenterTetanus1(25);
    line10.setOutreachTetanus1(25);
    line10.setHealthCenterTetanus2To5(1);
    line10.setOutreachTetanus2To5(1);

    VaccinationAdultCoverage adultCoverage = new VaccinationAdultCoverage();
    adultCoverage.setAdultCoverageLineItems(Arrays.asList(line9, line10));
    adultCoverage.setOpenedVialLineItems(Collections.singletonList(line8));

    original = new FacilityDistribution(
        facilityVisit, epiUse, refrigerators, epiInventory, fullCoverage, childCoverage, adultCoverage
    );
    replacement = original.transform();

    handler = new FacilityDistributionEditHandler();
  }

  @Test
  public void shouldNotFindConflicts() throws Exception {
    // without changes
    FacilityDistributionEditResults results = handler.check(original, replacement);
    assertThat(results.isConflict(), is(false));
    assertThat(results.getDetails().size(), is(0));

    // with changes
    replacement.getFacilityVisit().getObservations().setValue("some very important observations");
    replacement.getEpiUse().getLineItems().get(0).getExpirationDate().setValue("05/2016");
    replacement.getFullCoverage().getFemaleMobileBrigadeReading().setValue("100");

    results = handler.check(original, replacement);
    assertThat(results.isConflict(), is(false));
    assertThat(results.getDetails().size(), is(3));

    for (FacilityDistributionEditDetail detail : results.getDetails()) {
      assertThat(detail.isConflict(), is(false));
      assertThat(detail.getDataScreen(), isOneOf("FacilityVisit", "EpiUseLineItem", "VaccinationFullCoverage"));
      assertThat(detail.getEditedItem(), isOneOf("observations", "expirationDate", "femaleOutreach"));
    }

  }

  @Test
  public void shouldFindConflicts() throws Exception {
    original.getFullCoverage().setFemaleOutreach(77);
    original.getFacilityVisit().setObservations(null);

    replacement.getFacilityVisit().getObservations().setValue("some very important observations");
    replacement.getEpiUse().getLineItems().get(0).getExpirationDate().setValue("05/2016");
    replacement.getFullCoverage().getFemaleMobileBrigadeReading().setValue("100");

    FacilityDistributionEditResults results = handler.check(original, replacement);
    assertThat(results.isConflict(), is(true));
    assertThat(results.getDetails().size(), is(3));

    for (FacilityDistributionEditDetail detail : results.getDetails()) {
      assertThat(detail.getDataScreen(), isOneOf("FacilityVisit", "EpiUseLineItem", "VaccinationFullCoverage"));
      assertThat(detail.getEditedItem(), isOneOf("observations", "expirationDate", "femaleOutreach"));

      if (detail.getDataScreen().equals("EpiUseLineItem")) {
        assertThat(detail.isConflict(), is(false));
      } else {
        assertThat(detail.isConflict(), is(true));
      }
    }
  }

}
