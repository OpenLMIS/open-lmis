package org.openlmis.vaccine.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.repository.mapper.DiseaseMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DiseaseRepositoryTest {

  @Mock
  DiseaseMapper mapper;

  @InjectMocks
  DiseaseRepository repository;

  @Test
  public void shouldUpdate() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setId(2L);
    repository.update(disease);
    verify(mapper).update(disease);
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineDisease disease = new VaccineDisease();

    repository.insert(disease);
    verify(mapper).insert(disease);
  }

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    repository.getById(2L);
    verify(mapper).getById(2L);
  }
}