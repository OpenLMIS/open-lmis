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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
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
}
