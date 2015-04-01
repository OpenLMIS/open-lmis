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
import org.openlmis.equipment.domain.ProgramEquipmentProduct;
import org.openlmis.equipment.repository.ProgramEquipmentProductRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentProductServiceTest {

  @Mock
  ProgramEquipmentProductRepository repository;

  @InjectMocks
  ProgramEquipmentProductService service;

  @Test
  public void shouldGetByProgramEquipmentId() throws Exception {
    service.getByProgramEquipmentId(3L);
    verify(repository).getByProgramEquipmentId(3L);
  }

  @Test
  public void shouldSaveNewRecords() throws Exception {
    ProgramEquipmentProduct pp = new ProgramEquipmentProduct();
    service.Save(pp);
    verify(repository).insert(pp);
    verify(repository, never()).update(pp);
  }

  @Test
  public void shouldUpdaetRecords() throws Exception {
    ProgramEquipmentProduct pp = new ProgramEquipmentProduct();
    pp.setId(3L);
    service.Save(pp);

    verify(repository, never()).insert(pp);
    verify(repository).update(pp);
  }


  @Test
  public void shouldRemove() throws Exception{
    service.remove(3L);
    verify(repository).remove(3L);
  }

  @Test
  public void shouldRemoveEquipmentProducts() throws Exception {

  }

  @Test
  public void shouldGetAvailableProductsToLink() throws Exception {

  }
}