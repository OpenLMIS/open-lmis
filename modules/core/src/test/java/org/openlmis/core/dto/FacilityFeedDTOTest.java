/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

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
    assertThat(facilityFeedDTO.getFacilityType(), is(facility.getFacilityType().getName()));
    assertThat(facilityFeedDTO.getDescription(), is(facility.getDescription()));
    assertThat(facilityFeedDTO.getGln(), is(facility.getGln()));
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
    assertThat(facilityFeedDTO.getSdp(), is(facility.getSdp()));
    assertThat(facilityFeedDTO.getHasElectricity(), is(facility.getHasElectricity()));
    assertThat(facilityFeedDTO.getOnline(), is(facility.getOnline()));
    assertThat(facilityFeedDTO.getHasElectronicSCC(), is(facility.getHasElectronicScc()));
    assertThat(facilityFeedDTO.getHasElectronicDAR(), is(facility.getHasElectronicDar()));
    assertThat(facilityFeedDTO.getActive(), is(facility.getActive()));
    assertThat(facilityFeedDTO.getGoLiveDate(), is(facility.getGoLiveDate()));
    assertThat(facilityFeedDTO.getGoDownDate(), is(facility.getGoDownDate()));
    assertThat(facilityFeedDTO.getSatellite(), is(facility.getSatellite()));
    assertThat(facilityFeedDTO.getVirtualFacility(), is(facility.getVirtualFacility()));
    assertThat(facilityFeedDTO.getParentFacility(), is(parentFacility.getCode()));
    assertThat(facilityFeedDTO.getComment(), is(facility.getComment()));
    assertThat(facilityFeedDTO.isEnabled(), is(true));
    assertThat(facilityFeedDTO.getModifiedDate(), is(facility.getModifiedDate()));
  }
}
