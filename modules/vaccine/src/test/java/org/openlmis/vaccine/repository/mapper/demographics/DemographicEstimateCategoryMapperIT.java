package org.openlmis.vaccine.repository.mapper.demographics

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DemographicEstimateCategoryMapperIT {

  @Autowired
  DemographicEstimateCategoryMapper mapper;

  private DemographicEstimateCategory getNewDemographicEstimateCategory() {
    DemographicEstimateCategory category = new DemographicEstimateCategory();
    category.setName("Children Below 6");
    category.setDescription("All Children below 6");
    category.setIsPrimaryEstimate(false);
    category.setDefaultConversionFactor(0.5D);
    return category;
  }

  @Test
  public void shouldGetAll() throws Exception {

    List<DemographicEstimateCategory> categories = mapper.getAll();
    assertThat(categories.size(), is(1));
  }

  @Test
  public void shouldGetById() {
    DemographicEstimateCategory category = getNewDemographicEstimateCategory();
    mapper.insert(category);

    DemographicEstimateCategory result = mapper.getById(category.getId());

    assertThat(result.getName(), is(category.getName()));
    assertThat(result.getDescription(), is(category.getDescription()));
    assertThat(result.getIsPrimaryEstimate(), is(category.getIsPrimaryEstimate()));
    assertThat(result.getDefaultConversionFactor(), is(category.getDefaultConversionFactor()));
  }

  @Test
  public void shouldInsert() {
    DemographicEstimateCategory category = getNewDemographicEstimateCategory();

    Integer result =  mapper.insert(category);
    assertThat(category.getId(), is(notNullValue()));
    assertThat(result, is(1));
  }

  @Test
  public void shouldUpdate() {
    DemographicEstimateCategory category = getNewDemographicEstimateCategory();
    Integer result =  mapper.insert(category);
    category.setName("a different name");
    category.setDescription("another description as well");

    Integer updateResult = mapper.update(category);
    DemographicEstimateCategory updatedCategory = mapper.getById(category.getId());

    assertThat(updateResult, is(1));
    assertThat(updatedCategory.getName(), is(category.getName()));
  }

}
