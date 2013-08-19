package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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

  FacilityFtpDetails facilityFtpDetails;

  @Before
  public void setUp() throws Exception {
    facilityFtpDetails = new FacilityFtpDetails("F10", "ftp-host", "ftp-port", "ftp-user", "ftp-password", "ftp-local-path");
    mapper.insert(facilityFtpDetails);
  }

  @Test
  public void shouldGetByFacilityCode() throws Exception {

    FacilityFtpDetails result = mapper.getByFacilityCode("F10");

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

    FacilityFtpDetails result = mapper.getByFacilityCode(facilityFtpDetails.getFacilityCode());

    assertThat(result.getLocalFolderPath(), is("new-path"));
    assertThat(result.getServerHost(), is("new-host"));
    assertThat(result.getServerPort(), is("new-port"));
    assertThat(result.getUserName(), is("new-user"));
    assertThat(result.getPassword(), is("new-password"));
  }
}
