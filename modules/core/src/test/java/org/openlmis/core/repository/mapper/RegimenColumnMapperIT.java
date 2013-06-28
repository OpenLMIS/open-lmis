/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.db.categories.IntegrationTests;
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

    RegimenColumn regimenColumn = new RegimenColumn(program.getId(), "testName", "testLabel", "numeric", true);

    mapper.insert(regimenColumn);

    RegimenColumn resultColumn = mapper.getRegimenColumnByNameAndProgramId("testName", program.getId());

    assertThat(resultColumn, is(regimenColumn));
  }

  @Test
  public void shouldGetAllRegimenColumnsByProgram() throws Exception {

    RegimenColumn regimenColumn1 = new RegimenColumn(program.getId(), "testName1", "testLabel1", "numeric", true);
    RegimenColumn regimenColumn2 = new RegimenColumn(program.getId(), "testName2", "testLabel2", "text", false);

    mapper.insert(regimenColumn1);

    mapper.insert(regimenColumn2);

    List<RegimenColumn> resultColumns = mapper.getAllRegimenColumnsByProgramId(program.getId());

    assertThat(resultColumns.get(0), is(regimenColumn1));
    assertThat(resultColumns.get(1), is(regimenColumn2));
  }

  @Test
  public void shouldUpdateAllRegimenColumnsByProgram() throws Exception {

    RegimenColumn regimenColumn = new RegimenColumn(program.getId(), "testName", "testLabel", "numeric", true);

    mapper.insert(regimenColumn);

    regimenColumn = mapper.getRegimenColumnByNameAndProgramId("testName", program.getId());

    regimenColumn.setLabel("newLabel");
    regimenColumn.setVisible(false);
    regimenColumn.setDataType("text");

    mapper.update(regimenColumn);

    RegimenColumn updatedColumn = mapper.getRegimenColumnByNameAndProgramId("testName", program.getId());

    assertThat(updatedColumn.getLabel(), is("newLabel"));
    assertThat(updatedColumn.getName(), is("testName"));
    assertThat(updatedColumn.getVisible(), is(false));
    assertThat(updatedColumn.getDataType(), is("text"));
  }

}