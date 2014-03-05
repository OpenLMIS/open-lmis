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
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RequisitionStatusChangeMapperIT {

  @Autowired
  RequisitionMapper requisitionMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  private RequisitionStatusChangeMapper mapper;

  @Autowired
  UserMapper userMapper;

  private ProcessingSchedule processingSchedule;
  private Facility facility;
  private ProcessingPeriod processingPeriod;
  private SupervisoryNode supervisoryNode;
  private RequisitionStatusChange statusChange;
  private Rnr requisition;
  private User user;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod = insertPeriod("Period 1");
    supervisoryNode = insertSupervisoryNode();
    Program program = new Program();
    program.setId(1L);

    user = make(a(defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.periodId, processingPeriod.getId()),
        with(RequisitionBuilder.facility, facility), with(RequisitionBuilder.program, program),
        with(RequisitionBuilder.modifiedBy, user.getId())));
    requisitionMapper.insert(requisition);

    String name = "some random name";
    statusChange = new RequisitionStatusChange(requisition, name);
  }

  @Test
  public void shouldLogStatusChangesToRequisition() throws Exception {
    mapper.insert(statusChange);

    List<RequisitionStatusChange> statusChanges = mapper.getByRnrId(requisition.getId());

    assertThat(statusChanges.size(), is(1));
    RequisitionStatusChange change = statusChanges.get(0);
    assertThat(change.getCreatedDate(), is(notNullValue()));
    change.setCreatedDate(null);
  }

  @Test
  public void shouldGetStatusChangesForAnRnr() throws Exception {
    mapper.insert(statusChange);
    statusChange.setStatus(SUBMITTED);
    mapper.insert(statusChange);
    statusChange.setStatus(AUTHORIZED);
    mapper.insert(statusChange);

    List<RequisitionStatusChange> statusChanges = mapper.getByRnrId(requisition.getId());

    assertThat(statusChanges.size(), is(3));
    assertThat(statusChanges.get(0).getCreatedBy().getFirstName(), is(user.getFirstName()));
    assertThat(statusChanges.get(0).getCreatedBy().getLastName(), is(user.getLastName()));
    assertThat(statusChanges.get(0).getUserName(), is("some random name"));
    assertThat(statusChanges.get(0).getCreatedBy().getId(), is(user.getId()));

    assertStatusPresent(statusChanges, INITIATED);
    assertStatusPresent(statusChanges, SUBMITTED);
    assertStatusPresent(statusChanges, AUTHORIZED);
  }

  private void assertStatusPresent(List<RequisitionStatusChange> statusChanges, RnrStatus status) {
    boolean present = false;
    for (RequisitionStatusChange change : statusChanges) {
      if (change.getStatus().equals(status))
        present = true;
    }
    assertTrue(present);
  }

  private ProcessingPeriod insertPeriod(String name) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
        with(scheduleId, processingSchedule.getId()),
        with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode() {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }
}
