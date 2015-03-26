package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.repository.mapper.VendorUserMapper;



@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorUserRepositoryTest {

  @Mock
  VendorUserMapper mapper;

  @InjectMocks
  VendorUserRepository repository;

  @Test
  public void shouldGetAllUsersForVendor() throws Exception {
    repository.getAllUsersForVendor(1L);
    verify(mapper).getAllUsersForVendor(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    VendorUser vendorUser = new VendorUser();
    repository.insert(vendorUser);

    verify(mapper).insert(vendorUser);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VendorUser vendorUser = new VendorUser();
    repository.update(vendorUser);

    verify(mapper).update(vendorUser);
  }

  @Test
  public void shouldRemoveVendorUserAssociation() throws Exception {
    repository.removeVendorUserAssociation(1L, 2L);
    verify(mapper).remove(1L, 2L);
  }

  @Test
  public void shouldGetAllUsersAvailableForVendor() throws Exception {
    repository.getAllUsersAvailableForVendor();
    verify(mapper).getAllUsersAvailableForVendor();
  }
}