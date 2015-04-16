package org.openlmis.vaccine.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.repository.mapper.ProductDoseMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductDoseRepositoryTest {

  @Mock
  ProductDoseMapper mapper;

  @InjectMocks
  ProductDoseRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    VaccineProductDose dose = new VaccineProductDose();
    repository.insert(dose);
    verify(mapper).insert(any(VaccineProductDose.class));
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineProductDose dose = new VaccineProductDose();
    repository.update(dose);
    verify(mapper).update(any(VaccineProductDose.class));
  }

  @Test
  public void shouldGetDosesForProduct() throws Exception {
    repository.getDosesForProduct(1L, 2L);
    verify(mapper).getDoseSettingByProduct(1L, 2L);
  }


  @Test
  public void shouldGetProgramProductDoses() throws Exception {
    repository.getProgramProductDoses(2L);
    verify(mapper).getProgramProductDoses(2L);
  }
}