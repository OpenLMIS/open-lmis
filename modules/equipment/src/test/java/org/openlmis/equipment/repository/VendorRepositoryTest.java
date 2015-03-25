package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.repository.mapper.VendorMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorRepositoryTest {

  @Mock
  VendorMapper mapper;

  @InjectMocks
  VendorRepository repository;

  @Test
  public void shouldGetById() throws Exception {
    repository.getById(1L);
    verify(mapper).getById(1L);
  }

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldInsert() throws Exception {
    Vendor vendor = new Vendor();
    vendor.setName("name");
    repository.insert(vendor);

    verify(mapper).insert(vendor);
  }

  @Test
  public void shouldUpdate() throws Exception {
    Vendor vendor = new Vendor();
    vendor.setName("name");
    repository.update(vendor);

    verify(mapper).update(vendor);

  }

  @Test
  public void shouldRemove() throws Exception {
    repository.remove(1L);
    verify(mapper).remove(1L);
  }
}