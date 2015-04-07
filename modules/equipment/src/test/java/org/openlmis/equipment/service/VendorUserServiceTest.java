package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.repository.VendorUserRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorUserServiceTest {

  @Mock
  VendorUserRepository repository;

  @InjectMocks
  VendorUserService service;

  @Test
  public void shouldGetAllUsersForVendor() throws Exception {
    service.getAllUsersForVendor(3L);
    verify(repository).getAllUsersForVendor(3L);
  }

  @Test
  public void shouldSave() throws Exception {
    VendorUser vendorUser = new VendorUser();
    service.save(vendorUser);
    verify(repository).insert(vendorUser);
    verify(repository, never()).update(vendorUser);
  }

  @Test
  public void shouldSaveUpdates() throws Exception {
    VendorUser vendorUser = new VendorUser();
    vendorUser.setId(20L);
    service.save(vendorUser);
    verify(repository, never()).insert(vendorUser);
    verify(repository).update(vendorUser);
  }

  @Test
  public void shouldRemoveVendorUserAssociation() throws Exception {
    service.removeVendorUserAssociation(2L, 30L);
    verify(repository).removeVendorUserAssociation(2L, 30L);
  }

  @Test
  public void shouldGetAllUsersAvailableForVendor() throws Exception {
    service.getAllUsersAvailableForVendor();
    verify(repository).getAllUsersAvailableForVendor();
  }
}