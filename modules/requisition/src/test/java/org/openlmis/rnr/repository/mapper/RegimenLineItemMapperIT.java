package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RegimenLineItemMapperIT {

  @Autowired
  private RegimenLineItemMapper mapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RegimenMapper regimenMapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private QueryExecutor queryExecutor;

  RegimenLineItem regimenLineItem;
  Rnr rnr;

  @Before
  public void setUp() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);

    rnr = new Rnr(facility.getId(), 2L, processingPeriod.getId(), 1L, 1L);
    rnr.setStatus(RnrStatus.INITIATED);
    requisitionMapper.insert(rnr);
    RegimenCategory category = new RegimenCategory("categoryCode", "categoryName", 1);
    category.setId(1L);
    regimenLineItem = new RegimenLineItem(rnr.getId(), category, 1L, 1L);
  }

  @Test
  public void shouldInsertRegimenLineItem() throws Exception {
    mapper.insert(regimenLineItem);

    assertNotNull(regimenLineItem.getId());
  }

  @Test
  public void shouldGetRegimenLineItems() throws Exception {
    mapper.insert(regimenLineItem);

    List<RegimenLineItem> returnedRegimenLineItems = mapper.getRegimenLineItemsByRnrId(rnr.getId());

    assertThat(returnedRegimenLineItems.get(0).getRnrId(), is(regimenLineItem.getRnrId()));
    assertThat(returnedRegimenLineItems.get(0).getCode(), is(regimenLineItem.getCode()));
    assertThat(returnedRegimenLineItems.get(0).getName(), is(regimenLineItem.getName()));
    assertThat(returnedRegimenLineItems.get(0).getCategory().getName(), is(regimenLineItem.getCategory().getName()));
    assertThat(returnedRegimenLineItems.get(0).getCategory().getDisplayOrder(), is(regimenLineItem.getCategory().getDisplayOrder()));
    assertThat(returnedRegimenLineItems.get(0).getRegimenDisplayOrder(), is(regimenLineItem.getRegimenDisplayOrder()));
  }

  @Test
  public void shouldUpdateRegimenLineItem() throws Exception {

    mapper.insert(regimenLineItem);

    regimenLineItem.setPatientsToInitiateTreatment(100);
    regimenLineItem.setPatientsOnTreatment(1000);
    regimenLineItem.setPatientsStoppedTreatment(200);
    regimenLineItem.setRemarks("Remarks");
    regimenLineItem.setModifiedBy(1L);

    ResultSet resultSetBeforeUpdate = queryExecutor.execute("SELECT * from regimen_line_items where id=?", Arrays.asList(regimenLineItem.getId()));
    resultSetBeforeUpdate.next();

    mapper.update(regimenLineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * from regimen_line_items where id=?", Arrays.asList(regimenLineItem.getId()));
    resultSet.next();

    assertThat(resultSet.getInt("patientsToInitiateTreatment"), is(100));
    assertThat(resultSet.getInt("patientsOnTreatment"), is(1000));
    assertThat(resultSet.getInt("patientsStoppedTreatment"), is(200));
    assertThat(resultSet.getString("remarks"), is("Remarks"));
    assertTrue(resultSet.getDate("modifiedDate") != resultSetBeforeUpdate.getDate("modifiedDate"));
    assertThat(resultSet.getLong("modifiedBy"), is(1L));

  }
}
