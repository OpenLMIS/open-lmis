/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
