package org.openlmis.core.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.mapper.VendorMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VendorRepositoryTest {


  @Mock
  VendorMapper vendorMapper;

  @InjectMocks
  VendorRepository vendorRepository;

  @Test
  public void shouldGetVendor(){

    Vendor expectedVendor = new Vendor();
    when(vendorMapper.getByName("vendor")).thenReturn(expectedVendor);

    Vendor vendor = vendorRepository.getByName("vendor");

    assertThat(vendor, is(expectedVendor));
    verify(vendorMapper).getByName("vendor");

  }
}
