package org.openlmis.core.domain;


import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VendorTest {

  private Vendor vendor;

  @Before
  public void setUp() throws Exception {
    vendor = new Vendor();
    vendor.setId(1);
    vendor.setAuthToken(UUID.randomUUID().toString());
    vendor.setActive(true);
    vendor.setName("test vendor");
  }

  @Test
  public void shouldValidateVendorAndReturnFalseIfIdNull() throws Exception {
    vendor.setName(null);

    assertThat(vendor.isValid(), is(false));
  }

  @Test
  public void shouldValidateVendorAndReturnFalseIfAuthTokenNull() throws Exception {
    vendor.setAuthToken(null);

    assertThat(vendor.isValid(), is(false));
  }

  @Test
  public void shouldValidateVendorAndReturnTrueIfValid() throws Exception {
    assertThat(vendor.isValid(), is(true));
  }
}
