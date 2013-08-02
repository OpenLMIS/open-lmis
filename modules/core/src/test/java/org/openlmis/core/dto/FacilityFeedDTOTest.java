package org.openlmis.core.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class FacilityFeedDTOTest {

  @Test
  //TODO write separate test for boolean fields
  public void shouldFillDTOFromGivenFacility() throws Exception {

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    Facility parentFacility = new Facility();
    parentFacility.setCode("Parent Facility");

    FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility, parentFacility);

    assertThat(facilityFeedDTO.getCode(), is(facility.getCode()));
    assertThat(facilityFeedDTO.getName(), is(facility.getName()));
    assertThat(facilityFeedDTO.getType(), is(facility.getFacilityType().getName()));
    assertThat(facilityFeedDTO.getDescription(), is(facility.getDescription()));
    assertThat(facilityFeedDTO.getGLN(), is(facility.getGln()));
    assertThat(facilityFeedDTO.getMainPhone(), is(facility.getMainPhone()));
    assertThat(facilityFeedDTO.getFax(), is(facility.getFax()));
    assertThat(facilityFeedDTO.getAddress1(), is(facility.getAddress1()));
    assertThat(facilityFeedDTO.getAddress2(), is(facility.getAddress2()));
    assertThat(facilityFeedDTO.getGeographicZone(), is(facility.getGeographicZone().getName()));
    assertThat(facilityFeedDTO.getCatchmentPopulation(), is(facility.getCatchmentPopulation()));
    assertThat(facilityFeedDTO.getLatitude(), is(facility.getLatitude()));
    assertThat(facilityFeedDTO.getLongitude(), is(facility.getLongitude()));
    assertThat(facilityFeedDTO.getAltitude(), is(facility.getAltitude()));
    assertThat(facilityFeedDTO.getOperatedBy(), is(facility.getOperatedBy().getText()));
    assertThat(facilityFeedDTO.getColdStorageGrossCapacity(), is(facility.getColdStorageGrossCapacity()));
    assertThat(facilityFeedDTO.getColdStorageNetCapacity(), is(facility.getColdStorageNetCapacity()));
    assertThat(facilityFeedDTO.getSuppliesOthers(), is(facility.getSuppliesOthers()));
    assertThat(facilityFeedDTO.getIsSDP(), is(facility.getSdp()));
    assertThat(facilityFeedDTO.getHasElectricity(), is(facility.getHasElectricity()));
    assertThat(facilityFeedDTO.getIsOnline(), is(facility.getOnline()));
    assertThat(facilityFeedDTO.getHasElectronicSCC(), is(facility.getHasElectronicScc()));
    assertThat(facilityFeedDTO.getHasElectronicDAR(), is(facility.getHasElectronicDar()));
    assertThat(facilityFeedDTO.getActive(), is(facility.getActive()));
    assertThat(facilityFeedDTO.getGoLiveDate(), is(facility.getGoLiveDate()));
    assertThat(facilityFeedDTO.getGoDownDate(), is(facility.getGoDownDate()));
    assertThat(facilityFeedDTO.getSatelliteFacility(), is(facility.getSatellite()));
    assertThat(facilityFeedDTO.getVirtualFacility(), is(facility.getVirtualFacility()));
    assertThat(facilityFeedDTO.getParentFacility(), is(parentFacility.getCode()));
    assertThat(facilityFeedDTO.getComments(), is(facility.getComment()));
    assertThat(facilityFeedDTO.isDataReportable(), is(true));
    assertThat(facilityFeedDTO.getModifiedDate(), is(facility.getModifiedDate()));
  }
}
