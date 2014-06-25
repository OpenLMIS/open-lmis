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

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.FacilityBuilder.parentFacilityId;
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

  Facility facility;

  @Before
  public void setUp() throws Exception {

    facility = make(a(defaultFacility));
    requisitionGroup = make(a(defaultRequisitionGroup));

    facilityMapper.insert(facility);
    requisitionGroupMapper.insert(requisitionGroup);

    requisitionGroupMember = new RequisitionGroupMember();
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
  public void shouldDeleteRequisitionGroupToFacilityMapping() {
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    requisitionGroupMemberMapper.deleteMembersFor(facility);

    assertThat(requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(facility.getId()).size(), is(0));
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

  @Test
  public void shouldGetAllRequisitionGroupMembersByFacilityId() throws Exception {
    requisitionGroupMemberMapper.insert(requisitionGroupMember);
    List<RequisitionGroupMember> actualMembers = requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(facility.getId());

    assertThat(actualMembers.size(), is(1));
    assertThat(actualMembers.get(0).getRequisitionGroup(), is(requisitionGroupMember.getRequisitionGroup()));
    assertThat(actualMembers.get(0).getFacility().getId(), is(requisitionGroupMember.getFacility().getId()));
  }

  @Test
  public void shouldDeleteVirtualFacilityMembersFromRequisitionGroup() throws Exception {
    Facility parentFacility = make(a(defaultFacility, with(FacilityBuilder.code, "PF")));
    facilityMapper.insert(parentFacility);
    Facility virtualFacility = make(a(defaultFacility, with(FacilityBuilder.code, "VF"), with(parentFacilityId, parentFacility.getId())));
    facilityMapper.insert(virtualFacility);
    requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(virtualFacility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setModifiedBy(1L);

    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    requisitionGroupMemberMapper.deleteMembersForVirtualFacility(parentFacility);

    List<RequisitionGroupMember> requisitionGroupMembers = requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(virtualFacility.getId());

    assertThat(requisitionGroupMembers.size(), is(0));
  }

  @Test
  public void shouldCopyRequisitionGroupMembersFromParentToVirtualFacilities() throws Exception {
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    Facility virtualFacility1 = make(a(defaultFacility, with(FacilityBuilder.code, "VF1"), with(parentFacilityId, facility.getId())));
    facilityMapper.insert(virtualFacility1);

    Facility virtualFacility2 = make(a(defaultFacility, with(FacilityBuilder.code, "VF2"), with(parentFacilityId, facility.getId())));
    facilityMapper.insert(virtualFacility2);

    Facility rootFacility = make(a(defaultFacility, with(FacilityBuilder.code, "root")));
    facilityMapper.insert(rootFacility);
    requisitionGroupMember.setFacility(rootFacility);

    requisitionGroupMemberMapper.insert(requisitionGroupMember);
    requisitionGroupMemberMapper.copyToVirtualFacilities(facility);

    List<RequisitionGroupMember> member1 = requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(virtualFacility1.getId());
    assertThat(member1.size(), is(1));
    List<RequisitionGroupMember> member2 = requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(virtualFacility2.getId());
    assertThat(member2.size(), is(1));
    List<RequisitionGroupMember> requisitionGroupMembersRoot = requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(rootFacility.getId());
    assertThat(requisitionGroupMembersRoot.size(), is(1));
  }

  @Test
  public void shouldGetAllRequisitionGroupMembersByRequisitionGroupId() throws Exception {
    requisitionGroupMemberMapper.insert(requisitionGroupMember);
    List<RequisitionGroupMember> actualMembers = requisitionGroupMemberMapper.getMembersBy(requisitionGroupMember.getRequisitionGroup().getId());

    assertThat(actualMembers.size(), is(1));
    assertThat(actualMembers.get(0).getFacility().getId(), is(requisitionGroupMember.getFacility().getId()));
    assertThat(actualMembers.get(0).getFacility().getFacilityType().getName(), is("Warehouse"));
    assertThat(actualMembers.get(0).getFacility().getName(), is("Apollo Hospital"));
    assertThat(actualMembers.get(0).getFacility().getCode(), is("F10010"));
    assertThat(actualMembers.get(0).getFacility().getEnabled(), is(true));
    assertThat(actualMembers.get(0).getFacility().getGeographicZone().getName(), is(requisitionGroupMember.getFacility().getGeographicZone().getName()));
  }

  @Test
  public void shouldDeleteRequisitionGroupMemberByRequisitionGroup() {

    Long requisitionGroupId = requisitionGroup.getId();
    requisitionGroupMemberMapper.insert(requisitionGroupMember);
    List<RequisitionGroupMember> members = requisitionGroupMemberMapper.getMembersBy(requisitionGroupId);
    assertThat(members.size(), is(1));

    requisitionGroupMemberMapper.deleteMemberForGroup(requisitionGroupId);

    members = requisitionGroupMemberMapper.getMembersBy(requisitionGroupId);
    assertThat(members.size(), is(0));
  }
}
