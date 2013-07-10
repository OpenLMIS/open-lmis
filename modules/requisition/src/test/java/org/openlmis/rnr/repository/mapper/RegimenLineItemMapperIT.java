package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
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

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertNotNull;
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

    rnr = new Rnr(facility.getId(), 2L, processingPeriod.getId(), 1L);
    rnr.setStatus(RnrStatus.INITIATED);
    requisitionMapper.insert(rnr);
    RegimenCategory category = new RegimenCategory("categoryCode", "categoryName", 1);
    category.setId(1L);
    Regimen regimen = new Regimen("Name", "code", 2L, true, category, 1);
    regimenMapper.insert(regimen);
    regimenLineItem = new RegimenLineItem(rnr.getId(), regimen);
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
    assertThat(returnedRegimenLineItems.get(0).getRegimen().getCode(), is(regimenLineItem.getRegimen().getCode()));
    assertThat(returnedRegimenLineItems.get(0).getRegimen().getName(), is(regimenLineItem.getRegimen().getName()));
    assertThat(returnedRegimenLineItems.get(0).getRegimen().getCategory().getName(), is(regimenLineItem.getRegimen().getCategory().getName()));
    assertThat(returnedRegimenLineItems.get(0).getRegimen().getCategory().getDisplayOrder(), is(regimenLineItem.getRegimen().getCategory().getDisplayOrder()));
    assertThat(returnedRegimenLineItems.get(0).getRegimen().getDisplayOrder(), is(regimenLineItem.getRegimen().getDisplayOrder()));
  }

  @Test
  public void shouldUpdateRegimenLineItem() throws Exception {
    mapper.insert(regimenLineItem);

    regimenLineItem.setPatientsToInitiateTreatment(100);
    regimenLineItem.setPatientsOnTreatment(1000);
    regimenLineItem.setPatientsStoppedTreatment(200);
    regimenLineItem.setRemarks("Remarks");

    mapper.update(regimenLineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * from regimen_line_items where id=?", Arrays.asList(regimenLineItem.getId()));
    resultSet.next();

    assertThat(resultSet.getInt("patientsToInitiateTreatment"), is(100));
    assertThat(resultSet.getInt("patientsOnTreatment"), is(1000));
    assertThat(resultSet.getInt("patientsStoppedTreatment"), is(200));
    assertThat(resultSet.getString("remarks"), is("Remarks"));

  }
}
