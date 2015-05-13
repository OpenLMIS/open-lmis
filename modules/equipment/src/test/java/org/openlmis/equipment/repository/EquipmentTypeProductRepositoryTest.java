package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentTypeProduct;
import org.openlmis.equipment.repository.mapper.EquipmentTypeProductMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentTypeProductRepositoryTest {

  @Mock
  EquipmentTypeProductMapper mapper;

  @InjectMocks
  EquipmentProductRepository repository;

  @Test
  public void shouldGetByProgramEquipmentId() throws Exception {
    repository.getByProgramEquipmentId(1L);

    verify(mapper).getByProgramEquipmentId(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentTypeProduct product = new EquipmentTypeProduct();

    repository.insert(product);

    verify(mapper).insert(product);
  }

  @Test
  public void shouldUpdate() throws Exception {
    EquipmentTypeProduct product = new EquipmentTypeProduct();

    repository.update(product);

    verify(mapper).update(product);
  }

  @Test
  public void shouldRemove() throws Exception {
    repository.remove(5L);

    verify(mapper).remove(5L);
  }

  @Test
  public void shouldRemoveEquipmentProducts() throws Exception {
    repository.removeAllByEquipmentProducts(66L);
    verify(mapper).removeByEquipmentProducts(66L);
  }

  @Test
  public void shouldGetAvailableProductsToLink() throws Exception {
    repository.getAvailableProductsToLink(3L, 5L);
    verify(mapper).getAvailableProductsToLink(3L, 5L);
  }
}