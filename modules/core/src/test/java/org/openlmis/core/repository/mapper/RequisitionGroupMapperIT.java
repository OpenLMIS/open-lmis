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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.*;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RequisitionGroupMapperIT {

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  private RequisitionGroup requisitionGroup;
  private SupervisoryNode supervisoryNode;
  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode = make(a(defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);
    requisitionGroup = make(a(defaultRequisitionGroup));
  }

  @Test
  public void shouldInsertRequisitionGroup() throws Exception {
    requisitionGroup.setSupervisoryNode(supervisoryNode);

    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroup.getId());

    assertThat(resultRequisitionGroup.getCode(), is(REQUISITION_GROUP_CODE));
    assertThat(requisitionGroup.getId(), is(notNullValue()));
    assertThat(resultRequisitionGroup.getName(), is(REQUISITION_GROUP_NAME));
    assertThat(resultRequisitionGroup.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
    assertThat(resultRequisitionGroup.getSupervisoryNode().getName(), is(supervisoryNode.getName()));
    assertThat(resultRequisitionGroup.getSupervisoryNode().getCode(), is(supervisoryNode.getCode()));
  }

  @Test
  public void shouldUpdateRequisitionGroup() throws Exception {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    requisitionGroup.setCode("updated code");
    requisitionGroup.setName("updated name");
    requisitionGroup.setDescription("updated description");

    requisitionGroupMapper.update(requisitionGroup);

    RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroup.getId());

    assertThat(resultRequisitionGroup.getCode(), is("updated code"));
    assertThat(resultRequisitionGroup.getName(), is("updated name"));
    assertThat(resultRequisitionGroup.getDescription(), is("updated description"));
  }

  @Test
  public void shouldGetRequisitionGroupsForSupervisoryNodes() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.getRequisitionGroupBySupervisoryNodes("{" + supervisoryNode.getId() + "}");

    assertThat(requisitionGroups.size(), is(1));
  }

  @Test
  public void shouldGetRequisitionGroupByProgramIdAndFacilityId() throws Exception {
    requisitionGroupMapper.insert(requisitionGroup);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
    requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
    requisitionGroupProgramSchedule.setProcessingSchedule(processingSchedule);
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setModifiedBy(1L);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    assertThat(requisitionGroupMapper.getRequisitionGroupForProgramAndFacility(requisitionGroupProgramSchedule.getProgram(),
      requisitionGroupMember.getFacility()), is(requisitionGroup));
  }

  @Test
  public void shouldGetRequisitionByCode() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);
    assertThat(requisitionGroupMapper.getByCode(requisitionGroup.getCode()), is(requisitionGroup));
  }

  @Test
  public void shouldGetRequisitionGroupsBasedOnNameSearchWithMemberCountSortedByGroupName() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG Second")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.searchByGroupName("Rg", new Pagination(1, 10));

    assertThat(requisitionGroups.size(), is(2));
    assertThat(requisitionGroups.get(0).getMemberCount(), is(1));
    assertThat(requisitionGroups.get(0).getCode(), is("RG1"));
    assertThat(requisitionGroups.get(1).getCode(), is("RG2"));
    assertNull(requisitionGroups.get(1).getMemberCount());
  }

  @Test
  public void shouldGetRequisitionGroupsBasedOnNameSearchWithMemberCountSortedByNodeName() {
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG NAME")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.searchByGroupName("Rg", new Pagination(1, 10));

    assertThat(requisitionGroups.size(), is(2));
    assertThat(requisitionGroups.get(1).getCode(), is("RG1"));
    assertThat(requisitionGroups.get(1).getMemberCount(), is(1));
    assertThat(requisitionGroups.get(0).getCode(), is("RG2"));
    assertNull(requisitionGroups.get(0).getMemberCount());
  }

  @Test
  public void shouldGetRequisitionGroupsBasedOnNodeNameSearchWithMemberCountSortedByGroupName() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG Second")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.searchByNodeName("Ap", new Pagination(1, 10));

    assertThat(requisitionGroups.size(), is(2));
    assertThat(requisitionGroups.get(0).getMemberCount(), is(1));
    assertThat(requisitionGroups.get(0).getCode(), is("RG1"));
    assertThat(requisitionGroups.get(1).getCode(), is("RG2"));
    assertNull(requisitionGroups.get(1).getMemberCount());
  }

  @Test
  public void shouldGetRequisitionGroupsBasedOnNodeNameSearchWithMemberCountSortedByNodeName() {
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG NAME")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.searchByNodeName("ap", new Pagination(1, 10));

    assertThat(requisitionGroups.size(), is(1));
    assertThat(requisitionGroups.get(0).getCode(), is("RG2"));
    assertNull(requisitionGroups.get(0).getMemberCount());
  }

  @Test
  public void shouldGetResultCountBasedOnNodeNameSearch() {
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG NAME")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    Integer count = requisitionGroupMapper.getTotalRecordsForSearchOnNodeName("ap");

    assertThat(count, is(1));
  }

  @Test
  public void shouldGetResultCountBasedOnGroupNameSearch() {
    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup requisitionGroup1 = make(a(defaultRequisitionGroup, with(code, "RG2"), with(name, "RG NAME")));
    requisitionGroup1.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup1);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    Integer count = requisitionGroupMapper.getTotalRecordsForSearchOnGroupName("rg");

    assertThat(count, is(2));
  }

}
