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
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
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
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RequisitionGroupMemberMapperIT {

  RequisitionGroupMember requisitionGroupMember;
  RequisitionGroup requisitionGroup;

  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));

  @Before
  public void setUp() throws Exception {
    requisitionGroupMember = new RequisitionGroupMember();

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    requisitionGroup = make(a(defaultRequisitionGroup));

    facilityMapper.insert(facility);
    requisitionGroupMapper.insert(requisitionGroup);

    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setModifiedBy(1L);

    processingScheduleMapper.insert(processingSchedule);
  }

  @Test
  public void shouldInsertRequisitionGroupToFacilityMapping() throws Exception {
    int status = requisitionGroupMemberMapper.insert(requisitionGroupMember);
    assertThat(status, is(1));
  }

  @Test
  public void shouldGetProgramsMappedToRequisitionGroupByFacilityId() throws Exception {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
    requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
    requisitionGroupProgramSchedule.setProcessingSchedule(processingSchedule);
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<Long> programIds = requisitionGroupMemberMapper.getRequisitionGroupProgramIdsForFacilityId(requisitionGroupMember.getFacility().getId());

    assertThat(programIds.size(), is(1));
    assertThat(programIds.get(0), is(requisitionGroupProgramSchedule.getProgram().getId()));
  }
}
