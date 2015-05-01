/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.RegimenColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RegimenColumnMapperIT {

  @Autowired
  RegimenColumnMapper mapper;

  @Autowired
  ProgramMapper programMapper;

  Program program;

  @Before
  public void setUp() throws Exception {
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void shouldInsertRegimenColumnsForAProgram() throws Exception {

    RegimenColumn regimenColumn = new RegimenColumn(1L, "testName", "testLabel", "numeric", Boolean.TRUE,1, 1L);
    regimenColumn.setCreatedBy(1L);

    mapper.insert(regimenColumn, 1L);

    List<RegimenColumn> columnsByProgramId = mapper.getAllRegimenColumnsByProgramId(1L);

    assertThat(columnsByProgramId, hasItem(regimenColumn));
    assertThat(columnsByProgramId.size(), is(1));
  }

  @Test
  public void shouldGetAllRegimenColumnsByProgram() throws Exception {

    Long programId = 1L;
    RegimenColumn regimenColumn1 = new RegimenColumn(programId, "testName1", "testLabel1", "numeric", Boolean.TRUE,1 ,1L);
    RegimenColumn regimenColumn2 = new RegimenColumn(programId, "testName2", "testLabel2", "text", Boolean.FALSE,2, 1L);

    mapper.insert(regimenColumn1, programId);

    mapper.insert(regimenColumn2, programId);

    List<RegimenColumn> resultColumns = mapper.getAllRegimenColumnsByProgramId(programId);

    assertThat(resultColumns.get(0), is(regimenColumn1));
    assertThat(resultColumns.get(1), is(regimenColumn2));
  }

  @Test
  public void shouldUpdateAllRegimenColumnsByProgram() throws Exception {

    RegimenColumn regimenColumn = new RegimenColumn(program.getId(), "testName", "testLabel", "numeric", Boolean.TRUE,1, 1L);
    regimenColumn.setDisplayOrder(1);

    mapper.insert(regimenColumn, program.getId());

    regimenColumn = mapper.getAllRegimenColumnsByProgramId(program.getId()).get(0);

    regimenColumn.setLabel("newLabel");
    regimenColumn.setVisible(false);
    regimenColumn.setDataType("text");
    regimenColumn.setModifiedBy(2L);

    mapper.update(regimenColumn);

    RegimenColumn updatedColumn = mapper.getAllRegimenColumnsByProgramId(program.getId()).get(0);

    assertThat(updatedColumn.getLabel(), is("newLabel"));
    assertThat(updatedColumn.getName(), is("testName"));
    assertThat(updatedColumn.getVisible(), is(false));
    assertThat(updatedColumn.getDataType(), is("text"));
    assertThat(updatedColumn.getModifiedBy(), is(2L));
  }

  @Test
  public void shouldGetMasterRegimenColumns() throws Exception {

    List<RegimenColumn> regimenColumns = mapper.getMasterRegimenColumns();

    assertThat(regimenColumns.size(), is(13));
  }
}
