/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.equipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.ColdChainEquipmentPqsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ColdChainEquipmentPsqStatusMapperIT {

  @Autowired
  ColdChainEquipmentPqsStatusMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;


  @Test
  public void shouldGetCCEPqsStatusById() throws Exception {
    ColdChainEquipmentPqsStatus status = new ColdChainEquipmentPqsStatus();
    status.setName("Test");
    mapper.insert(status);

    ColdChainEquipmentPqsStatus result = mapper.getById(status.getId());
    assertEquals(result.getName(), status.getName());
  }

  @Test
  public void shouldGetAllCCEPqsStatus() throws Exception {

    List<ColdChainEquipmentPqsStatus> designations=mapper.getAll();
    assertEquals(designations.size(), 3);
  }

}