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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProcessingScheduleMapperIT {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  private ProcessingSchedule processingSchedule;

  @Before
  public void setUp() {
    processingSchedule = make(a(defaultProcessingSchedule));
  }

  @Test
  public void shouldGetIdByCode() {
    processingScheduleMapper.insert(processingSchedule);
    assertThat(processingScheduleMapper.getIdForCode("Q1stM"), is(processingSchedule.getId()));
  }

  @Test
  public void shouldGetByCode() {
    processingScheduleMapper.insert(processingSchedule);
    assertThat(processingScheduleMapper.getByCode("Q1stM"), is(processingSchedule));
  }

  @Test
  public void shouldInsertASchedule() {
    processingSchedule = make(a(defaultProcessingSchedule,
      with(code, "test code"),
      with(name, "test name"),
      with(description, "desc"),
      with(modifiedBy, 1L)));

    Integer insertionCount = processingScheduleMapper.insert(processingSchedule);

    assertThat(insertionCount, is(1));
    assertThat(processingSchedule.getId(), is(notNullValue()));

    processingSchedule = processingScheduleMapper.get(processingSchedule.getId());

    assertThat(processingSchedule.getCode(), is("test code"));
    assertThat(processingSchedule.getName(), is("test name"));
    assertThat(processingSchedule.getDescription(), is("desc"));
    assertThat(processingSchedule.getModifiedBy(), is(1L));
    assertThat(processingSchedule.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldGetAllSchedules() {
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule,
      with(code, "test code"),
      with(name, "test name"),
      with(description, "desc"),
      with(modifiedBy, 1L)));
    processingScheduleMapper.insert(processingSchedule);
    processingScheduleMapper.insert(processingSchedule2);

    List<ProcessingSchedule> processingSchedules = processingScheduleMapper.getAll();
    assertThat(processingSchedules.size(), is(2));
    assertThat(processingSchedules.get(0).getCode(), is(processingSchedule.getCode()));
    assertThat(processingSchedules.get(1).getCode(), is(processingSchedule2.getCode()));
  }

  @Test
  public void shouldGetScheduleById() {
    processingScheduleMapper.insert(processingSchedule);
    processingSchedule = processingScheduleMapper.get(processingSchedule.getId());

    assertThat(processingSchedule.getCode(), is("Q1stM"));
    assertThat(processingSchedule.getName(), is("QuarterMonthly"));
    assertThat(processingSchedule.getDescription(), is("QuarterMonth"));
  }

  @Test
  public void shouldUpdateAnExistingSchedule() {
    processingScheduleMapper.insert(processingSchedule);

    processingSchedule.setCode("Q1stM_updated");
    processingSchedule.setName("QuarterMonthly_Updated");
    processingSchedule.setDescription("QuarterMonthDesc_Updated");
    processingSchedule.setModifiedBy(2L);

    Integer updateCount = processingScheduleMapper.update(processingSchedule);

    assertThat(updateCount, is(1));
    ProcessingSchedule updatedSchedule = processingScheduleMapper.get(processingSchedule.getId());
    assertThat(updatedSchedule.getCode(), is("Q1stM_updated"));
    assertThat(updatedSchedule.getName(), is("QuarterMonthly_Updated"));
    assertThat(updatedSchedule.getDescription(), is("QuarterMonthDesc_Updated"));
    assertThat(updatedSchedule.getModifiedBy(), is(2L));
    // TODO : need to figure out a way to flush session cache before committing so that the updated default value can be fetched
    // assertThat(updatedSchedule.getModifiedDate(), is(not(creationDate)));
  }

  @Test
  public void shouldNotUpdateAnExistingScheduleWithDuplicateCode() {
    processingScheduleMapper.insert(processingSchedule);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(code, "Code2")));
    processingScheduleMapper.insert(processingSchedule2);
    processingSchedule.setCode("Code2");

    expectedEx.expect(DuplicateKeyException.class);

    processingScheduleMapper.update(processingSchedule);
  }

  @Test
  public void shouldNotInsertAScheduleWithExistingCode() {
    processingScheduleMapper.insert(processingSchedule);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(code, "Code2")));
    processingScheduleMapper.insert(processingSchedule2);
    processingSchedule.setCode("Code2");

    expectedEx.expect(DuplicateKeyException.class);

    processingScheduleMapper.update(processingSchedule);
  }
}
