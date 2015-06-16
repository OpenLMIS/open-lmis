package org.openlmis.vaccine.repository.demographics;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.repository.mapper.demographics.DemographicEstimateCategoryMapper;

import java.util.List;

import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DemographicEstimateCategoryRepositoryTest {

  @Mock
  DemographicEstimateCategoryMapper mapper;

  @InjectMocks
  DemographicEstimateCategoryRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    List<DemographicEstimateCategory> categories = repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    DemographicEstimateCategory category = repository.getById(1L);
    verify(mapper).getById(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    DemographicEstimateCategory category = new DemographicEstimateCategory();
    repository.insert(category);
    verify(mapper).insert(category);
  }

  @Test
  public void shouldUpdate() throws Exception {
    DemographicEstimateCategory category = new DemographicEstimateCategory();
    repository.update(category);
    verify(mapper).update(category);
  }
}