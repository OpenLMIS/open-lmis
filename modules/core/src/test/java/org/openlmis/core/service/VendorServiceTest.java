package org.openlmis.core.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.VendorRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class VendorServiceTest{

  @Mock
  VendorRepository vendorRepository;

  @InjectMocks
  VendorService service;

  @Test
  public void shouldGetVendor() throws Exception {
    Vendor expectedVendor = new Vendor();
    when(vendorRepository.getByName("vendor")).thenReturn(expectedVendor);


    Vendor vendor = service.getByName("vendor");

    assertThat(vendor, is(expectedVendor));
    verify(vendorRepository).getByName("vendor");
  }
}
