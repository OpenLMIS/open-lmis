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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityOperatorMapperIT {

  @Autowired
  private FacilityOperatorMapper mapper;

  @Test
  public void shouldInsertAndUpdateFacilityOperatorByCode() {
    FacilityOperator facOp = new FacilityOperator();
    facOp.setCode("someCode");
    facOp.setText("someText");
    facOp.setDisplayOrder(1);

    // insert
    mapper.insert(facOp);

    // test insert
    FacilityOperator retFacOp = mapper.getByCode(facOp.getCode());
    assertThat(retFacOp, is(facOp));

    // update
    retFacOp.setCode("aNewCode");
    retFacOp.setText("aNewText");
    retFacOp.setDisplayOrder(2);
    mapper.update(retFacOp);

    // test update
    FacilityOperator updatedFacOp = mapper.getByCode(retFacOp.getCode());
    assertThat(updatedFacOp, is(retFacOp));
  }

  @Test
  public void shouldIgnoreCaseWhenFindByCode() {
    FacilityOperator facOp = new FacilityOperator();
    facOp.setCode("somecode");
    facOp.setText("sometext");
    facOp.setDisplayOrder(1);

    mapper.insert(facOp);
    
    FacilityOperator retFacOp = mapper.getByCode("SOMECODE");
    assertThat(retFacOp, notNullValue());
    assertThat(retFacOp, is(facOp));
  }

  @Test
  public void shouldReturnNullWithInvalidId() {
    assertThat(mapper.getById(9919L), nullValue());
  }

  @Test
  public void shouldGetAllByDisplayOrder() {
    List<FacilityOperator> allManualSort = mapper.getAll();

    Collections.sort(allManualSort, new Comparator<FacilityOperator>() {
      @Override
      public int compare(FacilityOperator o1, FacilityOperator o2) {
        return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
      }
    });

    List<FacilityOperator> all = mapper.getAll();
    assertThat(all.size(), greaterThan(1));
    assertThat(all, equalTo(allManualSort));
  }
}
