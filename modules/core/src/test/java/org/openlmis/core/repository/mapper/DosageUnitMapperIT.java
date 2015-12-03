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
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class DosageUnitMapperIT {

  @Autowired
  private DosageUnitMapper duMapper;

  @Test
  public void shouldGetAllDosageUnits() throws Exception {
    List<DosageUnit> result = duMapper.getAll();
    assertThat(result.size(), is(7));
  }  
  
  @Test  
  public void shouldInsertDosageUnitByCode() {
    DosageUnit du = new DosageUnit();
    du.setCode("du code");
    du.setDisplayOrder(1);
    duMapper.insert(du);

    DosageUnit returnedDu = duMapper.getByCode("DU CODE");

    assertThat(returnedDu.getCode(), is(du.getCode()));
    assertThat(returnedDu.getDisplayOrder(), is(du.getDisplayOrder()));
  }

  @Test
  public void shouldUpdateDosageUnit() {
    DosageUnit du = new DosageUnit();
    du.setCode("someCode");
    du.setDisplayOrder(1);
    duMapper.insert(du);

    DosageUnit returnedDu = duMapper.getByCode("someCode");
    returnedDu.setCode("someOtherCode");
    duMapper.update(returnedDu);

    DosageUnit updatedDu = duMapper.getByCode("someOtherCode");
    assertThat(updatedDu.getCode(), is(returnedDu.getCode()));
    assertThat(updatedDu.getDisplayOrder(), is(returnedDu.getDisplayOrder()));
  }
}