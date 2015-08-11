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

package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.repository.mapper.DonorMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DonorRepositoryTest {

  @Mock
  DonorMapper mapper;

  @InjectMocks
  DonorRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldGetAllWithDetails() throws Exception {
    repository.getAllWithDetails();
    verify(mapper).getAllWithDetails();
  }

  @Test
  public void shouldInsert() throws Exception {
    Donor donor = new Donor();
    repository.insert(donor);
    verify(mapper).insert(donor);
  }

  @Test
  public void shouldUpdate() throws Exception {
    Donor donor = new Donor();
    repository.update(donor);
    verify(mapper).update(donor);
  }

  @Test
  public void shouldGetDonorById() throws Exception {
    repository.getDonorById(1L);
    verify(mapper).getById(1L);
  }

  @Test
  public void shouldRemoveDonor() throws Exception {
    repository.removeDonor(4L);
    verify(mapper).remove(4L);
  }
}