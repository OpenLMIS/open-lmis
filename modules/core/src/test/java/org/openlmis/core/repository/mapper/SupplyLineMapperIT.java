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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class SupplyLineMapperIT {

  @Autowired
  SupplyLineMapper mapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  FacilityMapper facilityMapper;

  SupplyLine supplyLine;

  Facility facility;

  SupervisoryNode supervisoryNode;

  Program program;


  @Before
  public void setUp() throws Exception {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(facility);
    supplyLine.setProgram(program);
    supplyLine.setSupervisoryNode(supervisoryNode);
    supplyLine.setExportOrders(Boolean.TRUE);
  }

  @Test
  public void shouldInsertSupplyLine() {
    Integer id = mapper.insert(supplyLine);
    assertNotNull(id);
  }

  @Test
  public void shouldReturnSupplyLineForASupervisoryNodeAndProgram() {
    mapper.insert(supplyLine);

    SupplyLine returnedSupplyLine = mapper.getSupplyLineBy(supervisoryNode, program);

    assertThat(returnedSupplyLine.getId(), is(supplyLine.getId()));
  }

  @Test
  public void shouldUpdateSupplyLine() throws Exception {
    mapper.insert(supplyLine);

    supplyLine.setDescription("New Description");
    supplyLine.setExportOrders(Boolean.FALSE);
    supplyLine.setModifiedBy(2L);

    mapper.update(supplyLine);

    SupplyLine supplyLineFromDataBase = mapper.getSupplyLineBy(supervisoryNode, program);

    assertThat(supplyLineFromDataBase.getDescription(), is("New Description"));
    assertThat(supplyLineFromDataBase.getModifiedBy(), is(2L));
    assertThat(supplyLineFromDataBase.getExportOrders(), is(Boolean.FALSE));
  }

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeProgramAndFacility() throws Exception {
    mapper.insert(supplyLine);

    SupplyLine supplyLineReturned = mapper.getSupplyLineBySupervisoryNodeProgramAndFacility(supplyLine);

    assertThat(supplyLineReturned.getProgram().getId(), is(program.getId()));
    assertThat(supplyLineReturned.getSupplyingFacility().getId(), is(facility.getId()));
    assertThat(supplyLineReturned.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
  }

  @Test
  public void shouldGetSupplyLineByIdFilledWithSupplyingFacility() throws Exception {
    mapper.insert(supplyLine);

    SupplyLine supplyLineReturned = mapper.getById(supplyLine.getId());

    assertThat(supplyLineReturned.getProgram().getId(), is(program.getId()));
    assertThat(supplyLineReturned.getSupplyingFacility().getId(), is(facility.getId()));
    assertThat(supplyLineReturned.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
    assertThat(supplyLineReturned.getSupplyingFacility().getName(), is(facility.getName()));
  }


}
