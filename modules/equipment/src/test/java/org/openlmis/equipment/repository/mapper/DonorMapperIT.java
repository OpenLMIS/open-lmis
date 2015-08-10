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

package org.openlmis.equipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.builder.DonorBuilder;
import org.openlmis.equipment.domain.Donor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DonorMapperIT {

  @Autowired
  private DonorMapper mapper;


  @Test
  public void testGetAll() throws Exception {
    List<Donor> donors = mapper.getAll();
    assertEquals( 1, donors.size());
  }

  @Test
  public void testGetAllWithDetails() throws Exception {
    Donor donor =  make(a(DonorBuilder.defaultDonor));
    mapper.insert(donor);
    List<Donor> donors = mapper.getAllWithDetails();
    assertEquals( 2, donors.size());
  }

  @Test
  public void testInsert() throws Exception {
    Donor donor = make(a(DonorBuilder.defaultDonor));

    List<Donor> donors = mapper.getAll();
    int size = donors.size();
    mapper.insert(donor);
    donors = mapper.getAll();
    assertEquals(size + 1, donors.size());

    Donor donor2 = mapper.getById(donor.getId());
    assertEquals(donor.getCode(), donor2.getCode());
  }

  @Test
  public void testUpdate() throws Exception {
    Donor donor = make(a(DonorBuilder.defaultDonor));
    mapper.insert(donor);
    Long id = donor.getId();
    donor = mapper.getById(id);
    donor.setCode("2nd Code");
    mapper.update(donor);

    donor = mapper.getById(id);
    assertEquals("2nd Code", donor.getCode());
  }

  @Test
  public void testRemove() throws Exception {
    Donor donor = make(a(DonorBuilder.defaultDonor));
    mapper.insert(donor);

    List<Donor> donors = mapper.getAll();
    int size = donors.size();
    mapper.remove(donor.getId());
    donors = mapper.getAll();
    assertEquals(size -1, donors.size());
  }

  @Test
  public void testGetById() throws Exception {
    Donor donor = make(a(DonorBuilder.defaultDonor));
    mapper.insert(donor);

    Donor donor2 = mapper.getById(donor.getId());
    assertEquals(donor.getCode(), donor2.getCode());
    assertEquals(donor.getLongName(), donor2.getLongName());

  }

  @Test
  public void testGetByCode() throws Exception {
    Donor donor = make(a(DonorBuilder.defaultDonor));
    mapper.insert(donor);

    Donor donor2 = mapper.getByCode(donor.getCode());
    assertEquals(donor.getId(), donor2.getId());
  }
}