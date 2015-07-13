/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.config.VaccineIvdTabVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineIvdTabVisibilityMapperIT {

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  VaccineIvdTabVisibilityMapper mapper;

  Program program;

  @Before
  public void setup()throws Exception{
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void shouldGetTabVisibilityForProgram() throws Exception {

    VaccineIvdTabVisibility visibility = new VaccineIvdTabVisibility();
    visibility.setTab("STOCK_STATUS_TAB");
    visibility.setName("Any Name");
    visibility.setVisible(true);
    visibility.setProgramId(program.getId());

    mapper.insert(visibility);

    List<VaccineIvdTabVisibility> visibilities = mapper.getTabVisibilityForProgram(program.getId());
    assertThat(visibilities.size(), is(1));
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineIvdTabVisibility visibility = new VaccineIvdTabVisibility();
    visibility.setTab("STOCK_STATUS_TAB");
    visibility.setName("Any Name");
    visibility.setVisible(true);
    visibility.setProgramId(program.getId());

    Integer result = mapper.insert(visibility);
    assertThat(result, is(1));
    assertThat(visibility.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetTabVisibilityForNewProgram() throws Exception{
    List<VaccineIvdTabVisibility> tabVisibilities = mapper.getTabVisibilityForNewProgram();
    assertThat(tabVisibilities.size(), is(8));
  }

  @Test
  public void shouldUpdate() throws Exception {

    VaccineIvdTabVisibility visibility = new VaccineIvdTabVisibility();
    visibility.setTab("STOCK_STATUS_TAB");
    visibility.setName("Any Name");
    visibility.setVisible(true);
    visibility.setProgramId(program.getId());

    mapper.insert(visibility);

    visibility.setVisible( false );
    mapper.update(visibility);

    List<VaccineIvdTabVisibility> result = mapper.getTabVisibilityForProgram(program.getId());
    assertThat(result.get(0).getVisible(), is(visibility.getVisible()));
  }
}