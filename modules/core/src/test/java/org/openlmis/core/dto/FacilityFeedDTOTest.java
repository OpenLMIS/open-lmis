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

    FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility);

    assertThat(facilityFeedDTO.getId(), is(facility.getId()));
    assertThat(facilityFeedDTO.getCode(), is(facility.getCode()));
    assertThat(facilityFeedDTO.getName(), is(facility.getName()));
    assertThat(facilityFeedDTO.getTypeId(), is(facility.getFacilityType().getId()));
    assertThat(facilityFeedDTO.getDescription(), is(facility.getDescription()));
    assertThat(facilityFeedDTO.getGLN(), is(facility.getGln()));
    assertThat(facilityFeedDTO.getMainPhone(), is(facility.getMainPhone()));
    assertThat(facilityFeedDTO.getFax(), is(facility.getFax()));
    assertThat(facilityFeedDTO.getAddress1(), is(facility.getAddress1()));
    assertThat(facilityFeedDTO.getAddress2(), is(facility.getAddress2()));
    assertThat(facilityFeedDTO.getGeographicZoneID(), is(facility.getGeographicZone().getId()));
    assertThat(facilityFeedDTO.getCatchmentPopulation(), is(facility.getCatchmentPopulation()));
    assertThat(facilityFeedDTO.getLatitude(), is(facility.getLatitude()));
    assertThat(facilityFeedDTO.getLongitude(), is(facility.getLongitude()));
    assertThat(facilityFeedDTO.getAltitude(), is(facility.getAltitude()));
    assertThat(facilityFeedDTO.getOperatedBy(), is(facility.getOperatedBy().getText()));
    assertThat(facilityFeedDTO.getColdStorageGrossCapacity(), is(facility.getColdStorageGrossCapacity()));
    assertThat(facilityFeedDTO.getColdStorageNetCapacity(), is(facility.getColdStorageNetCapacity()));
    assertThat(facilityFeedDTO.isSuppliesOthers(), is((facility.getSuppliesOthers() != null) ? facility.getSuppliesOthers() : false));
    assertThat(facilityFeedDTO.isSDP(), is(facility.getSdp()));
    assertThat(facilityFeedDTO.isHasElectricity(), is((facility.getHasElectricity() != null) ? facility.getHasElectricity() : false));
    assertThat(facilityFeedDTO.isOnline(), is((facility.getOnline() != null) ? facility.getOnline() : false));
    assertThat(facilityFeedDTO.isHasElectronicSCC(), is((facility.getHasElectronicScc() != null) ? facility.getHasElectronicScc() : false));
    assertThat(facilityFeedDTO.isHasElectronicDAR(), is((facility.getHasElectronicDar() != null) ? facility.getHasElectronicDar() : false));
    assertThat(facilityFeedDTO.isActive(), is(facility.getActive()));
    assertThat(facilityFeedDTO.getGoLiveDate(), is(facility.getGoLiveDate()));
    assertThat(facilityFeedDTO.getGoDownDate(), is(facility.getGoDownDate()));
    assertThat(facilityFeedDTO.isSatelliteFacility(), is((facility.getSatellite() != null) ? facility.getSatellite() : false));
    assertThat(facilityFeedDTO.isVirtualFacility(), is((facility.getVirtualFacility() != null) ? facility.getVirtualFacility() : false));
    assertThat(facilityFeedDTO.getParentFacilityId(), is(facility.getParentFacilityId()));
    assertThat(facilityFeedDTO.getComments(), is(facility.getComment()));
    assertThat(facilityFeedDTO.isDoNotDisplay(), is(true));
    assertThat(facilityFeedDTO.getModifiedDate(), is(facility.getModifiedDate()));
  }
}
