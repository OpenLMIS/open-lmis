package org.openlmis.core.repository.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.utils.mapper.TestVendorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class VendorMapperIT {

  @Autowired
  VendorMapper mapper;

  @Autowired
  TestVendorMapper testVendorMapper;

  @Test
  public void shouldInsertVendor() throws Exception {
    Vendor vendor = new Vendor("vendor", true);
    testVendorMapper.insert(vendor);

    Vendor actualVendor = mapper.getByName("vendor");
    assertThat(actualVendor.getAuthToken(), is(nullValue()));
    assertThat(actualVendor.getId(), is(vendor.getId()));
  }

  @Test
  public void shouldNotGetInActiveVendors() throws Exception {
    Vendor vendor = new Vendor("vendor", false);
    testVendorMapper.insert(vendor);

    assertThat(mapper.getByName("vendor"), is(nullValue()));
  }

  @Test
  public void shouldGetToken() throws Exception {
    Vendor vendor = new Vendor("vendor", true);
    testVendorMapper.insert(vendor);
    Vendor returnedVendor = testVendorMapper.get(vendor.getId());

    String authToken = mapper.getToken(returnedVendor.getName());
    assertThat(authToken, is(returnedVendor.getAuthToken()));
  }
}
