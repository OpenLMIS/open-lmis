package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.repository.VendorRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorServiceTest {

  @Mock
  VendorRepository repository;

  @InjectMocks
  VendorService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(3L);
    verify(repository).getById(3L);
  }

  @Test
  public void shouldSave() throws Exception {
    Vendor vendor = new Vendor();
    service.save(vendor);
    verify(repository).insert(vendor);
    verify(repository, never()).update(any(Vendor.class));
  }

  @Test
  public void shouldSaveUpdate() throws Exception {
    Vendor vendor = new Vendor();
    vendor.setId(20L);
    service.save(vendor);
    verify(repository, never()).insert(vendor);
    verify(repository).update(any(Vendor.class));
  }

  @Test
  public void shouldRemoveVendor() throws Exception {
    service.removeVendor(20L);
    verify(repository).remove(20L);
  }
}