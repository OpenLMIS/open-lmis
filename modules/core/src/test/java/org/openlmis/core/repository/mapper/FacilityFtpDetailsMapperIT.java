/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityFtpDetailsMapperIT {

  @Autowired
  FacilityFtpDetailsMapper mapper;

  @Autowired
  FacilityMapper facilityMapper;

  FacilityFtpDetails facilityFtpDetails;
  Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    facilityFtpDetails = new FacilityFtpDetails(facility, "ftp-host", "ftp-port", "ftp-user", "ftp-password", "ftp-local-path");
    mapper.insert(facilityFtpDetails);
  }

  @Test
  public void shouldGetByFacilityId() throws Exception {

    FacilityFtpDetails result = mapper.getByFacilityId(facility);
    result.setFacility(facility);

    assertThat(result, is(facilityFtpDetails));
  }

  @Test
  public void shouldUpdate() throws Exception {

    facilityFtpDetails.setLocalFolderPath("new-path");

    facilityFtpDetails.setServerHost("new-host");

    facilityFtpDetails.setServerPort("new-port");

    facilityFtpDetails.setUserName("new-user");

    facilityFtpDetails.setPassword("new-password");

    mapper.update(facilityFtpDetails);

    FacilityFtpDetails result = mapper.getByFacilityId(facilityFtpDetails.getFacility());

    assertThat(result.getLocalFolderPath(), is("new-path"));
    assertThat(result.getServerHost(), is("new-host"));
    assertThat(result.getServerPort(), is("new-port"));
    assertThat(result.getUserName(), is("new-user"));
    assertThat(result.getPassword(), is("new-password"));
  }
}
