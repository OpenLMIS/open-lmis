package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProcessingSchedule;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProcessingScheduleMapperIT {
  @Rule
  public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();


  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;
  private ProcessingSchedule processingSchedule;

  @Before
  public void setUp() throws Exception {
    processingSchedule = make(a(defaultProcessingSchedule));
  }

  @Test
  public void shouldGetIdByCode() throws Exception {
    processingScheduleMapper.insert(processingSchedule);
    assertThat(processingSchedule.getId(), is(notNullValue()));
    assertThat(processingScheduleMapper.getIdForCode("Q1stM"), is(processingSchedule.getId()));
  }

  @Test
  public void shouldInsertASchedule() throws Exception {
    processingSchedule = make(a(defaultProcessingSchedule,
      with(code, "test code"),
      with(name, "test name"),
      with(description, "desc"),
      with(modifiedBy, 1)));

    Integer insertionCount = processingScheduleMapper.insert(processingSchedule);

    assertThat(insertionCount, is(1));
    assertThat(processingSchedule.getId(), is(notNullValue()));

    processingSchedule = processingScheduleMapper.get(processingSchedule.getId());

    assertThat(processingSchedule.getCode(), is("test code"));
    assertThat(processingSchedule.getName(), is("test name"));
    assertThat(processingSchedule.getDescription(), is("desc"));
    assertThat(processingSchedule.getModifiedBy(), is(1));
    assertThat(processingSchedule.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldGetAllSchedules() throws Exception {
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule,
      with(code, "test code"),
      with(name, "test name"),
      with(description, "desc"),
      with(modifiedBy, 1)));
    processingScheduleMapper.insert(processingSchedule);
    processingScheduleMapper.insert(processingSchedule2);

    List<ProcessingSchedule> processingSchedules = processingScheduleMapper.getAll();
    assertThat(processingSchedules.size(), is(2));
    assertThat(processingSchedules.get(0).getCode(), is(processingSchedule.getCode()));
    assertThat(processingSchedules.get(1).getCode(), is(processingSchedule2.getCode()));
  }

  @Test
  public void shouldGetScheduleById() throws Exception {
    processingScheduleMapper.insert(processingSchedule);
    processingSchedule = processingScheduleMapper.get(processingSchedule.getId());

    assertThat(processingSchedule.getCode(), is("Q1stM"));
    assertThat(processingSchedule.getName(), is("QuarterMonthly"));
    assertThat(processingSchedule.getDescription(), is("QuarterMonth"));
  }

  @Test
  public void shouldUpdateAnExistingSchedule() throws Exception {
    processingScheduleMapper.insert(processingSchedule);

    processingSchedule.setCode("Q1stM_updated");
    processingSchedule.setName("QuarterMonthly_Updated");
    processingSchedule.setDescription("QuarterMonthDesc_Updated");
    processingSchedule.setModifiedBy(2);

    Integer updateCount = processingScheduleMapper.update(processingSchedule);

    assertThat(updateCount, is(1));
    ProcessingSchedule updatedSchedule = processingScheduleMapper.get(processingSchedule.getId());
    assertThat(updatedSchedule.getCode(), is("Q1stM_updated"));
    assertThat(updatedSchedule.getName(), is("QuarterMonthly_Updated"));
    assertThat(updatedSchedule.getDescription(), is("QuarterMonthDesc_Updated"));
    assertThat(updatedSchedule.getModifiedBy(), is(2));
    // TODO : need to figure out a way to flush session cache before committing so that the updated default value can be fetched
    // assertThat(updatedSchedule.getModifiedDate(), is(not(creationDate)));
  }

  @Test
  public void shouldNotUpdateAnExistingScheduleWithDuplicateCode() throws Exception {
    processingScheduleMapper.insert(processingSchedule);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(code, "Code2")));
    processingScheduleMapper.insert(processingSchedule2);
    processingSchedule.setCode("Code2");

    expectedEx.expect(DuplicateKeyException.class);

    processingScheduleMapper.update(processingSchedule);
  }

  @Test
  public void shouldNotInsertAScheduleWithExistingCode() throws Exception {
    processingScheduleMapper.insert(processingSchedule);
    ProcessingSchedule processingSchedule2 = make(a(defaultProcessingSchedule, with(code, "Code2")));
    processingScheduleMapper.insert(processingSchedule2);
    processingSchedule.setCode("Code2");

    expectedEx.expect(DuplicateKeyException.class);

    processingScheduleMapper.update(processingSchedule);
  }
}
