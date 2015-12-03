/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RegimenCategoryMapperIT {

  @Autowired
  RegimenCategoryMapper regimenCategoryMapper;

  @Test
  public void shouldGetAllRegimenCategories() {
    List<RegimenCategory> regimenCategories = regimenCategoryMapper.getAll();
    assertThat(regimenCategories.size(), is(2));
    assertThat(regimenCategories.get(0).getCode(), is("ADULTS"));
  }

  @Test
  public void shouldGetAllRegimenCategoriesSorted() {
    List<RegimenCategory> all = regimenCategoryMapper.getAll();
    assertThat(all.size(), greaterThan(1));

    // manually sort by display order and then name
    Collections.sort(all, new Comparator<RegimenCategory>() {
      public int compare(RegimenCategory o1, RegimenCategory o2) {
        int onDispOrd = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
        if(onDispOrd != 0) return onDispOrd;
        return o1.getName().compareTo(o2.getName());
      }
    });

    List<RegimenCategory> allFromDb = regimenCategoryMapper.getAll();
    assertThat(allFromDb, is(all));
  }

  @Test
  public void shouldGetRegimenById() {
    RegimenCategory adultCategory = regimenCategoryMapper.getById(1L);
    assertThat(adultCategory.getCode(), is("ADULTS"));
  }


  @Test
  public void shouldInsertAndUpdateWithCaseInsensitiveCode() {
    RegimenCategory regCat = new RegimenCategory();
    regCat.setCode("somecode");
    regCat.setName("someName");
    regCat.setDisplayOrder(1);

    // insert and test case insensitive get by code
    regimenCategoryMapper.insert(regCat);
    RegimenCategory retRegCat = regimenCategoryMapper.getByCode("SOMECODE");
    assertThat(retRegCat, notNullValue());
    assertThat(retRegCat, is(regCat));

    // update something and test it
    retRegCat.setName("some other name");
    retRegCat.setDisplayOrder(2);
    regimenCategoryMapper.update(retRegCat);
    RegimenCategory updatedRegCat = regimenCategoryMapper.getByCode("SOMECODE");
    assertThat(updatedRegCat, is(retRegCat));
  }
}
