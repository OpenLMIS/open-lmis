package org.openlmis.core.repository.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.utils.mapper.TestVendorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.builder.UserBuilder.vendorId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class VendorMapperIT {

  @Autowired
  VendorMapper mapper;

  @Autowired
  TestVendorMapper testVendorMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  FacilityMapper facilityMapper;

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

  @Test
  public void shouldGetVendorForUserId() throws Exception {
    Vendor vendor = new Vendor("vendor");
    testVendorMapper.insert(vendor);
    User user = insertUserWithVendor(vendor);

    Vendor returnedVendor = mapper.getByUserId(user.getId());

    assertThat(returnedVendor, is(vendor));
  }

  private User insertUserWithVendor(Vendor vendor) {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    User user = make(a(defaultUser, with(vendorId, vendor.getId()), with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }
}
