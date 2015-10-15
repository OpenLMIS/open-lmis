/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.demographics.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.demographics.domain.EstimateCategory;
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
@ContextConfiguration(locations = "classpath*:test-applicationContext-demographics.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EstimateCategoryMapperIT {

  @Autowired
  EstimateCategoryMapper mapper;

  private EstimateCategory getNewDemographicEstimateCategory() {
    EstimateCategory category = new EstimateCategory();
    category.setName("Children Below 6");
    category.setDescription("All Children below 6");
    category.setIsPrimaryEstimate(false);
    category.setDefaultConversionFactor(0.5D);
    return category;
  }

  @Test
  public void shouldGetAll() throws Exception {

    List<EstimateCategory> categories = mapper.getAll();
    assertThat(categories.size(), is(1));
  }

  @Test
  public void shouldGetById() {
    EstimateCategory category = getNewDemographicEstimateCategory();
    mapper.insert(category);

    EstimateCategory result = mapper.getById(category.getId());

    assertThat(result.getName(), is(category.getName()));
    assertThat(result.getDescription(), is(category.getDescription()));
    assertThat(result.getIsPrimaryEstimate(), is(category.getIsPrimaryEstimate()));
    assertThat(result.getDefaultConversionFactor(), is(category.getDefaultConversionFactor()));
  }

  @Test
  public void shouldInsert() {
    EstimateCategory category = getNewDemographicEstimateCategory();

    Integer result = mapper.insert(category);
    assertThat(category.getId(), is(notNullValue()));
    assertThat(result, is(1));
  }

  @Test
  public void shouldUpdate() {
    EstimateCategory category = getNewDemographicEstimateCategory();
    Integer result = mapper.insert(category);
    category.setName("a different name");
    category.setDescription("another description as well");

    Integer updateResult = mapper.update(category);
    EstimateCategory updatedCategory = mapper.getById(category.getId());

    assertThat(updateResult, is(1));
    assertThat(updatedCategory.getName(), is(category.getName()));
  }

}
