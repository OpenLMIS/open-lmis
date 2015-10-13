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

package org.openlmis.demographics.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.repository.EstimateCategoryRepository;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EstimateCategoryServiceTest {

  @Mock
  EstimateCategoryRepository repository;

  @InjectMocks
  EstimateCategoryService service;

  @Test
  public void shouldGetAll() throws Exception {
    List<EstimateCategory> categories = service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    EstimateCategory category = service.getById(1L);
    verify(repository).getById(1L);
  }

  @Test
  public void shouldSave() throws Exception {
    EstimateCategory category1 = new EstimateCategory();
    EstimateCategory category2 = new EstimateCategory();
    category2.setId(2L);
    List<EstimateCategory> categories = asList(category1, category2);

    // hmm, this covers both conditions but would it be better to break it into 2 differnt tests?
    service.save(categories);

    verify(repository).insert(category1);
    verify(repository).update(category2);
  }
}