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
import org.openlmis.core.builder.SupplyLineBuilder;
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
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;

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
    assertThat(supplyLineReturned.getSupplyingFacility().getId(), is(facility.getId()));
    assertThat(supplyLineReturned.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
    assertThat(supplyLineReturned.getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLineReturned.getSupplyingFacility().getCode(), is(facility.getCode()));
  }

  @Test
  public void shouldGetPaginatedSupplyLinesSearchedBySupplyingFacilityName() throws Exception {
    String searchParam = "Apollo";

    Facility f100 = make(a(defaultFacility, with(FacilityBuilder.code, "F100"), with(name, "Facility100")));
    facilityMapper.insert(f100);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    SupplyLine supplyLine1 = createSupplyLine(supervisoryNode, program);
    mapper.insert(supplyLine1);

    SupplyLine supplyLine2 = createSupplyLine(supervisoryNode, hivProgram);
    mapper.insert(supplyLine2);

    SupplyLine supplyLine3 = createSupplyLine(supervisoryNode, malariaProgram);
    mapper.insert(supplyLine3);

    SupplyLine supplyLine4 = createSupplyLine(supervisoryNode, tbProgram);
    supplyLine4.setSupplyingFacility(f100);
    mapper.insert(supplyLine4);

    Pagination pagination = new Pagination(2, 2);
    List<SupplyLine> supplyLines = mapper.searchByFacilityName(searchParam, pagination);

    assertThat(supplyLines.size(), is(1));
    assertThat(supplyLines.get(0).getId(), is(supplyLine3.getId()));
  }

  @Test
  public void shouldGetCountOfRecordsWhenSearchedByFacilityName() throws Exception {
    String searchParam = "Apollo";

    Facility f100 = make(a(defaultFacility, with(FacilityBuilder.code, "F100"), with(name, "Facility100")));
    facilityMapper.insert(f100);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    SupplyLine supplyLine1 = createSupplyLine(supervisoryNode, program);
    mapper.insert(supplyLine1);

    SupplyLine supplyLine2 = createSupplyLine(supervisoryNode, hivProgram);
    mapper.insert(supplyLine2);

    SupplyLine supplyLine3 = createSupplyLine(supervisoryNode, malariaProgram);
    mapper.insert(supplyLine3);

    SupplyLine supplyLine4 = createSupplyLine(supervisoryNode, tbProgram);
    supplyLine4.setSupplyingFacility(f100);
    mapper.insert(supplyLine4);

    Integer count = mapper.getTotalSearchResultsByFacilityName(searchParam);

    assertThat(count, is(3));
  }

  @Test
  public void shouldGetPaginatedSupplyLinesSearchedBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);
    SupervisoryNode supervisoryNode3 = insertSupervisoryNode("N3", "Node3", facility);
    SupervisoryNode supervisoryNode4 = insertSupervisoryNode("N4", "Node4", facility);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    SupplyLine supplyLine1 = createSupplyLine(supervisoryNode, program);
    mapper.insert(supplyLine1);

    SupplyLine supplyLine2 = createSupplyLine(supervisoryNode2, hivProgram);
    mapper.insert(supplyLine2);

    SupplyLine supplyLine3 = createSupplyLine(supervisoryNode3, malariaProgram);
    mapper.insert(supplyLine3);

    SupplyLine supplyLine4 = createSupplyLine(supervisoryNode4, tbProgram);
    mapper.insert(supplyLine4);

    Pagination pagination = new Pagination(2, 2);
    List<SupplyLine> supplyLines = mapper.searchBySupervisoryNodeName(searchParam, pagination);

    assertThat(supplyLines.size(), is(1));
    assertThat(supplyLines.get(0).getId(), is(supplyLine4.getId()));
    assertThat(supplyLines.get(0).getDescription(), is(supplyLine4.getDescription()));
    assertThat(supplyLines.get(0).getProgram().getName(), is(tbProgram.getName()));
    assertThat(supplyLines.get(0).getSupplyingFacility().getName(), is(facility.getName()));
    assertThat(supplyLines.get(0).getSupervisoryNode().getName(), is(supervisoryNode4.getName()));
  }

  @Test
  public void shouldGetCountOfRecordsWhenSearchedBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";

    SupervisoryNode supervisoryNode2 = insertSupervisoryNode("N2", "Node2", facility);
    SupervisoryNode supervisoryNode3 = insertSupervisoryNode("N3", "Node3", facility);
    SupervisoryNode supervisoryNode4 = insertSupervisoryNode("N4", "Node4", facility);

    Program hivProgram = programMapper.getByCode("HIV");
    Program malariaProgram = programMapper.getByCode("MALARIA");
    Program tbProgram = programMapper.getByCode("TB");

    SupplyLine supplyLine1 = createSupplyLine(supervisoryNode, program);
    mapper.insert(supplyLine1);

    SupplyLine supplyLine2 = createSupplyLine(supervisoryNode2, hivProgram);
    mapper.insert(supplyLine2);

    SupplyLine supplyLine3 = createSupplyLine(supervisoryNode3, malariaProgram);
    mapper.insert(supplyLine3);

    SupplyLine supplyLine4 = createSupplyLine(supervisoryNode4, tbProgram);
    mapper.insert(supplyLine4);

    Integer count = mapper.getTotalSearchResultsBySupervisoryNodeName(searchParam);

    assertThat(count, is(3));
  }

  private SupplyLine createSupplyLine(SupervisoryNode supervisoryNode, Program program) {
    return make(a(defaultSupplyLine, with(SupplyLineBuilder.supervisoryNode, supervisoryNode),
      with(SupplyLineBuilder.facility, facility), with(SupplyLineBuilder.program, program)));
  }
}
