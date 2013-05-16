package org.openlmis.core.repository;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.mapper.VendorMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorRepositoryTest {


  @Mock
  VendorMapper vendorMapper;

  @InjectMocks
  VendorRepository vendorRepository;

  @Test
  public void shouldGetVendor() {

    Vendor expectedVendor = new Vendor();
    when(vendorMapper.getByName("vendor")).thenReturn(expectedVendor);

    Vendor vendor = vendorRepository.getByName("vendor");

    assertThat(vendor, is(expectedVendor));
    verify(vendorMapper).getByName("vendor");

  }

  @Test
  public void shouldGetAuthTokenForVendor() throws Exception {
    String vendor = "some vendor";
    when(vendorMapper.getToken(vendor)).thenReturn("some token");
    String token = vendorRepository.getToken(vendor);

    assertThat(token, is("some token"));
    verify(vendorMapper).getToken(vendor);
  }

  @Test
  public void shouldGetVendorForUserId() throws Exception {
    Vendor expectedVendor = new Vendor();
    when(vendorMapper.getByUserId(1L)).thenReturn(expectedVendor);

    Vendor vendor = vendorRepository.getByUserId(1L);

    assertThat(vendor, is(expectedVendor));
    verify(vendorMapper).getByUserId(1L);
  }
}
