package org.openlmis.vaccine.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.builders.VaccineDiseaseBuilder;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.repository.DiseaseRepository;
import org.openlmis.vaccine.repository.mapper.DiseaseMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DiseaseServiceTest {

  @Mock
  DiseaseRepository mapper;

  @InjectMocks
  DiseaseService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineDisease disease = make(a(VaccineDiseaseBuilder.defaultDisease));
    disease.setId(20L);
    service.save(disease);
    verify(mapper).update(disease);
    verify(mapper, never()).insert(any(VaccineDisease.class));
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineDisease disease = make(a(VaccineDiseaseBuilder.defaultDisease));
    service.save(disease);
    verify(mapper).insert(disease);
    verify(mapper, never()).update(any(VaccineDisease.class));
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(2L);
    verify(mapper).getById(2L);
  }
}