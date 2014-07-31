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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RequisitionGroupProgramScheduleMapperIT {

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  RequisitionGroupProgramSchedule requisitionGroupProgramSchedule;

  @Before
  public void setUp() throws Exception {
    requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    requisitionGroupProgramSchedule.setModifiedBy(1L);
    requisitionGroupProgramSchedule.setModifiedDate(new Date(0));

    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));

    requisitionGroupProgramSchedule.setRequisitionGroup(make(a(defaultRequisitionGroup)));

    requisitionGroupProgramSchedule.setDirectDelivery(true);

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    requisitionGroupProgramSchedule.setDropOffFacility(facility);

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    processingScheduleMapper.insert(schedule);

    requisitionGroupProgramSchedule.setProcessingSchedule(schedule);
  }

  @Test
  public void shouldInsertRGProgramSchedule() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    Integer recordCount = requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    assertThat(recordCount, is(1));
  }

  @Test
  public void shouldUpdateRGProgramSchedule() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    requisitionGroupProgramSchedule.setDirectDelivery(false);
    Facility dropOffFacility = new Facility();
    requisitionGroupProgramSchedule.setDropOffFacility(dropOffFacility);
    requisitionGroupProgramSchedule.setDirectDelivery(false);

    requisitionGroupProgramScheduleMapper.update(requisitionGroupProgramSchedule);

    assertThat(requisitionGroupProgramSchedule.getDropOffFacility(), is(dropOffFacility));
    assertThat(requisitionGroupProgramSchedule.isDirectDelivery(), is(false));
  }

  @Test
  public void shouldGetProgramIdsForRGById() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    List<Long> resultProgramId = requisitionGroupProgramScheduleMapper.getProgramIDsById(requisitionGroupProgramSchedule.getRequisitionGroup().getId());

    assertThat(resultProgramId.size(), is(1));
    assertThat(resultProgramId.get(0), is(requisitionGroupProgramSchedule.getProgram().getId()));
  }

  @Test
  public void shouldGetRequisitionGroupProgramScheduleForRequisitionGroupIdAndProgramId() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    RequisitionGroupProgramSchedule resultRequisitionGroupProgramSchedule = requisitionGroupProgramScheduleMapper.
      getScheduleForRequisitionGroupIdAndProgramId(
        requisitionGroupProgramSchedule.getRequisitionGroup().getId(), requisitionGroupProgramSchedule.getProgram().getId());

    assertThat(resultRequisitionGroupProgramSchedule.getProgram().getId(), is(requisitionGroupProgramSchedule.getProgram().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.getProcessingSchedule().getId(), is(requisitionGroupProgramSchedule.getProcessingSchedule().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.getRequisitionGroup().getId(), is(requisitionGroupProgramSchedule.getRequisitionGroup().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.isDirectDelivery(), is(requisitionGroupProgramSchedule.isDirectDelivery()));
    assertThat(resultRequisitionGroupProgramSchedule.getDropOffFacility().getId(), is(requisitionGroupProgramSchedule.getDropOffFacility().getId()));
  }

  @Test
  public void shouldGetRequisitionGroupProgramScheduleForRequisitionGroupCodeAndProgramCode() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    RequisitionGroupProgramSchedule resultRequisitionGroupProgramSchedule = requisitionGroupProgramScheduleMapper.
      getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode(),
        requisitionGroupProgramSchedule.getProgram().getCode());

    assertThat(resultRequisitionGroupProgramSchedule.getProgram().getId(), is(requisitionGroupProgramSchedule.getProgram().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.getProcessingSchedule().getId(), is(requisitionGroupProgramSchedule.getProcessingSchedule().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.getRequisitionGroup().getId(), is(requisitionGroupProgramSchedule.getRequisitionGroup().getId()));
    assertThat(resultRequisitionGroupProgramSchedule.isDirectDelivery(), is(requisitionGroupProgramSchedule.isDirectDelivery()));
    assertThat(resultRequisitionGroupProgramSchedule.getDropOffFacility().getId(), is(requisitionGroupProgramSchedule.getDropOffFacility().getId()));
  }

  @Test
  public void shouldGetRequisitionGroupProgramScheduleForRequisitionGroup() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    List<RequisitionGroupProgramSchedule> result = requisitionGroupProgramScheduleMapper.
      getByRequisitionGroupId(requisitionGroupProgramSchedule.getRequisitionGroup().getId());

    assertThat(result.size(), is(1));
    assertThat(result.get(0).getProgram(), is(requisitionGroupProgramSchedule.getProgram()));
    assertThat(result.get(0).getProcessingSchedule(), is(requisitionGroupProgramSchedule.getProcessingSchedule()));
    assertThat(result.get(0).isDirectDelivery(), is(requisitionGroupProgramSchedule.isDirectDelivery()));
    assertThat(result.get(0).getDropOffFacility().getId(), is(requisitionGroupProgramSchedule.getDropOffFacility().getId()));
    assertThat(result.get(0).getDropOffFacility().getName(), is(requisitionGroupProgramSchedule.getDropOffFacility().getName()));
    assertThat(result.get(0).getRequisitionGroup().getId(), is(requisitionGroupProgramSchedule.getRequisitionGroup().getId()));
  }

  @Test
  public void shouldDeleteRequisitionGroupProgramScheduleByRequisitionGroup() throws Exception {
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());
    requisitionGroupMapper.insert(requisitionGroupProgramSchedule.getRequisitionGroup());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
    requisitionGroupProgramScheduleMapper.deleteRequisitionGroupProgramSchedulesFor(requisitionGroupProgramSchedule.getRequisitionGroup().getId());

    List<RequisitionGroupProgramSchedule> result = requisitionGroupProgramScheduleMapper.
      getByRequisitionGroupId(requisitionGroupProgramSchedule.getRequisitionGroup().getId());

    assertThat(result.size(), is(0));
  }
}
