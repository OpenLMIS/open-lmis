package org.openlmis.core.repository.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class VendorMapperTest {

  @Autowired
  VendorMapper mapper;

  @Test
  public void shouldInsertVendor() throws Exception {
    Vendor vendor = new Vendor("vendor", true);
    mapper.insert(vendor);

    Vendor actualVendor = mapper.getByName(vendor.getName());
    assertThat(actualVendor.getAuthToken(), is(notNullValue()));
    actualVendor.setAuthToken(null);

    assertThat(actualVendor, is(vendor));
  }
}
