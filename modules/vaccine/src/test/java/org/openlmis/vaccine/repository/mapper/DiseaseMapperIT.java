package org.openlmis.vaccine.repository.mapper;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DiseaseMapperIT {

  @Autowired
  DiseaseMapper mapper;

  @Test
  public void shouldGetAll() throws Exception {
    List<VaccineDisease> result = mapper.getAll();
    assertThat(result.size(), is(7));
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    Integer count = mapper.insert(disease);
    assertThat(count, is(notNullValue()));
    assertThat(disease.getId(),is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    mapper.insert(disease);

    disease.setDescription("The new Description");
    mapper.update(disease);

    VaccineDisease returnedObject = mapper.getById(disease.getId());
    assertThat(returnedObject, is(disease));
  }

  @Test
  public void shouldGetById() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    mapper.insert(disease);

    VaccineDisease returnedObject = mapper.getById(disease.getId());
    assertThat(returnedObject, is(disease));
  }
}