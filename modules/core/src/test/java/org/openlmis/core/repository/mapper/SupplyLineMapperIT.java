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
import org.openlmis.core.context.CoreTestContext;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.name;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class SupplyLineMapperIT extends CoreTestContext {

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
    facility = make(a(defaultFacility));
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
    assertThat(supplyLineReturned.getProgram().getName(), is(program.getName()));
    assertThat(supplyLineReturned.getSupplyingFacility().getId(), is(facility.getId()));
    assertThat(supplyLineReturned.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
    assertThat(supplyLineReturned.getSupervisoryNode().getName(), is(supervisoryNode.getName()));
    assertThat(supplyLineReturned.getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLineReturned.getSupplyingFacility().getCode(), is(facility.getCode()));
  }

  @Test
  public void shouldGetPaginatedSupplyLinesSearchedBySupplyingFacilityName() throws Exception {
    String searchParam = "Apollo";
    String column = "facility";

    Facility f100 = make(a(defaultFacility, with(FacilityBuilder.code, "F100"), with(name, "Facility100")));
    facilityMapper.insert(f100);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    SupplyLine supplyLine1 = insertSupplyLine(facility, supervisoryNode, program);
    insertSupplyLine(facility, supervisoryNode, hivProgram);
    insertSupplyLine(facility, supervisoryNode, malariaProgram);
    insertSupplyLine(f100, supervisoryNode, tbProgram);

    Pagination pagination = new Pagination(2, 2);
    List<SupplyLine> supplyLines = mapper.search(searchParam, column, pagination);

    assertThat(supplyLines.size(), is(1));
    assertThat(supplyLines.get(0).getId(), is(supplyLine1.getId()));
    assertThat(supplyLines.get(0).getDescription(), is(supplyLine1.getDescription()));
    assertThat(supplyLines.get(0).getProgram().getName(), is(program.getName()));
    assertThat(supplyLines.get(0).getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLines.get(0).getSupervisoryNode().getName(), is(supervisoryNode.getName()));
  }

  @Test
  public void shouldGetCountOfRecordsWhenSearchedByFacilityName() throws Exception {
    String searchParam = "Apollo";
    String column = "facility";

    Facility f100 = make(a(defaultFacility, with(FacilityBuilder.code, "F100"), with(name, "Facility100")));
    facilityMapper.insert(f100);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    insertSupplyLine(facility, supervisoryNode, program);
    insertSupplyLine(facility, supervisoryNode, hivProgram);
    insertSupplyLine(facility, supervisoryNode, malariaProgram);
    insertSupplyLine(f100, supervisoryNode, tbProgram);

    Integer count = mapper.getSearchedSupplyLinesCount(searchParam, column);

    assertThat(count, is(3));
  }

  @Test
  public void shouldGetPaginatedSupplyLinesSearchedBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";
    String column = "supervisoryNode";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);
    SupervisoryNode supervisoryNode3 = insertSupervisoryNode("N3", "Node3", facility);
    SupervisoryNode supervisoryNode4 = insertSupervisoryNode("N4", "Node4", facility);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    insertSupplyLine(facility, supervisoryNode, program);
    insertSupplyLine(facility, supervisoryNode3, hivProgram);
    SupplyLine supplyLine = insertSupplyLine(facility, supervisoryNode4, malariaProgram);
    insertSupplyLine(facility, supervisoryNode2, tbProgram);

    Pagination pagination = new Pagination(2, 2);
    List<SupplyLine> supplyLines = mapper.search(searchParam, column, pagination);

    assertThat(supplyLines.size(), is(1));
    assertThat(supplyLines.get(0).getId(), is(supplyLine.getId()));
    assertThat(supplyLines.get(0).getDescription(), is(supplyLine.getDescription()));
    assertThat(supplyLines.get(0).getProgram().getName(), is(malariaProgram.getName()));
    assertThat(supplyLines.get(0).getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLines.get(0).getSupervisoryNode().getName(), is(supervisoryNode4.getName()));
  }

  @Test
  public void shouldGetCountOfRecordsWhenSearchedBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";
    String column = "supervisoryNode";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);
    SupervisoryNode supervisoryNode3 = insertSupervisoryNode("N3", "Node3", facility);
    SupervisoryNode supervisoryNode4 = insertSupervisoryNode("N4", "Node4", facility);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    insertSupplyLine(facility, supervisoryNode, program);
    insertSupplyLine(facility, supervisoryNode2, hivProgram);
    insertSupplyLine(facility, supervisoryNode3, malariaProgram);
    insertSupplyLine(facility, supervisoryNode4, tbProgram);

    Integer count = mapper.getSearchedSupplyLinesCount(searchParam, column);

    assertThat(count, is(3));
  }

  @Test
  public void shouldGetPaginatedSupplyLinesSearchedByProgramName() throws Exception {
    String searchParam = "mal";
    String column = "program";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);

    Program malaria = programMapper.getByCode("MALARIA");

    insertSupplyLine(facility, supervisoryNode, program);
    SupplyLine supplyLine2 = insertSupplyLine(facility, supervisoryNode2, malaria);
    SupplyLine supplyLine3 = insertSupplyLine(facility, supervisoryNode, malaria);
    insertSupplyLine(facility, supervisoryNode2, program);

    Pagination pagination = new Pagination(1, 2);
    List<SupplyLine> supplyLines = mapper.search(searchParam, column, pagination);

    assertThat(supplyLines.size(), is(2));

    assertThat(supplyLines.get(0).getId(), is(supplyLine3.getId()));
    assertThat(supplyLines.get(0).getDescription(), is(supplyLine3.getDescription()));
    assertThat(supplyLines.get(0).getProgram().getName(), is(malaria.getName()));
    assertThat(supplyLines.get(0).getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLines.get(0).getSupervisoryNode().getName(), is(supervisoryNode.getName()));

    assertThat(supplyLines.get(1).getId(), is(supplyLine2.getId()));
    assertThat(supplyLines.get(1).getDescription(), is(supplyLine2.getDescription()));
    assertThat(supplyLines.get(1).getProgram().getName(), is(malaria.getName()));
    assertThat(supplyLines.get(1).getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLines.get(1).getSupervisoryNode().getName(), is(supervisoryNode2.getName()));
  }

  @Test
  public void shouldGetCountOfRecordsWhenSearchedByProgramName() throws Exception {
    String searchParam = "mal";
    String column = "program";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);
    SupervisoryNode supervisoryNode3 = insertSupervisoryNode("N3", "Node3", facility);

    Program malaria = programMapper.getByCode("MALARIA");

    insertSupplyLine(facility, supervisoryNode, program);
    insertSupplyLine(facility, supervisoryNode2, malaria);
    insertSupplyLine(facility, supervisoryNode, malaria);
    insertSupplyLine(facility, supervisoryNode2, program);
    insertSupplyLine(facility, supervisoryNode3, malaria);

    Integer count = mapper.getSearchedSupplyLinesCount(searchParam, column);

    assertThat(count, is(3));
  }
}
