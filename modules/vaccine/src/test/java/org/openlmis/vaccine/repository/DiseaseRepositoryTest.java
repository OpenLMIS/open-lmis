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