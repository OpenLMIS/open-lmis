package org.openlmis.vaccine.service.demographics;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.repository.demographics.DemographicEstimateCategoryRepository;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DemographicEstimateCategoryServiceTest {

  @Mock
  DemographicEstimateCategoryRepository repository;

  @InjectMocks
  DemographicEstimateCategoryService service;

  @Test
  public void shouldGetAll() throws Exception {
    List<DemographicEstimateCategory> categories = service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    DemographicEstimateCategory category = service.getById(1L);
    verify(repository).getById(1L);
  }

  @Test
  public void shouldSave() throws Exception {
    DemographicEstimateCategory category1 = new DemographicEstimateCategory();
    DemographicEstimateCategory category2 = new DemographicEstimateCategory();
    category2.setId(2L);
    List<DemographicEstimateCategory> categories = asList(category1, category2);

    // hmm, this covers both conditions but would it be better to break it into 2 differnt tests?
    service.save(categories);

    verify(repository).insert(category1);
    verify(repository).update(category2);
  }
}