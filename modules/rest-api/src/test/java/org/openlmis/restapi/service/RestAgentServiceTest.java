/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.security.Principal;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.AgentBuilder.defaultCHW;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestAgentService.class)
public class RestAgentServiceTest {

  @Mock
  FacilityService facilityService;

  @InjectMocks
  RestAgentService restAgentService;

  @Mock
  private UserService userService;

  @Mock
  private ProgramSupportedService programSupportedService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  Principal principal;
  private User user;

  @Mock
  private RequisitionGroupMemberService requisitionGroupMemberService;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
    user = new User(2l, principal.getName());
  }

  @Test
  public void shouldCreateFacilityForCHW() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);

    Facility facility = mock(Facility.class);
    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    when(facility.getParentFacilityId()).thenReturn(baseFacility.getId());

    RequisitionGroup requisitionGroup = new RequisitionGroup();
    when(requisitionGroupMemberService.getAllRequisitionGroupMembersByFacility(baseFacility.getId())).
      thenReturn(asList(new RequisitionGroupMember(requisitionGroup, baseFacility)));

    restAgentService.create(agent, user.getId());

    verify(facility, times(2)).setCode(agent.getAgentCode());
    verify(facility).setParentFacilityId(baseFacility.getId());
    verify(facility).setName(agent.getAgentName());
    verify(facility).setFacilityType(baseFacility.getFacilityType());
    verify(facility).setMainPhone(agent.getPhoneNumber());
    verify(facility).setGeographicZone(baseFacility.getGeographicZone());
    verify(facility).setActive(Boolean.parseBoolean(agent.getActive()));
    verify(facility).setVirtualFacility(true);
    verify(facility).setSdp(true);
    verify(facility).setEnabled(true);
    verify(facility).setGoLiveDate(currentTimeStamp);
    verify(facilityService).save(facility);
    verify(requisitionGroupMemberService).getAllRequisitionGroupMembersByFacility(baseFacility.getId());
    ArgumentCaptor<RequisitionGroupMember> captor = ArgumentCaptor.forClass(RequisitionGroupMember.class);
    verify(requisitionGroupMemberService).save(captor.capture());
    assertThat(captor.getValue().getRequisitionGroup(), is(requisitionGroup));
    assertThat(captor.getValue().getFacility(), is(facility));
    assertThat(captor.getValue().getCreatedBy(), is(user.getId()));
    assertThat(captor.getValue().getModifiedBy(), is(user.getId()));
    verify(programSupportedService).updateSupportedPrograms(facility);
  }

  @Test
  public void shouldUpdateACHWFacility() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);

    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    Facility chwFacility = spy(new Facility());
    chwFacility.setVirtualFacility(true);
    chwFacility.setEnabled(true);
    chwFacility.setParentFacilityId(1l);
    whenNew(Facility.class).withNoArguments().thenReturn(chwFacility);
    when(facilityService.getByCode(chwFacility)).thenReturn(chwFacility);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);

    restAgentService.update(agent, user.getId());

    verify(chwFacility).setName(agent.getAgentName());
    verify(chwFacility).setMainPhone(agent.getPhoneNumber());
    verify(chwFacility).setActive(Boolean.parseBoolean(agent.getActive()));
    verify(chwFacility, times(2)).setParentFacilityId(baseFacility.getId());
    verify(chwFacility).setGeographicZone(baseFacility.getGeographicZone());
    verify(chwFacility).setFacilityType(baseFacility.getFacilityType());
    verify(facilityService).update(chwFacility);
    verify(requisitionGroupMemberService, never()).deleteMembersFor(chwFacility);
  }

  @Test
  public void shouldUpdateACHWFacilityAndRequisitionGroupIfParentChanges() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);

    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    Facility chwFacility = spy(new Facility());
    chwFacility.setVirtualFacility(true);
    chwFacility.setEnabled(true);
    chwFacility.setParentFacilityId(3l);
    whenNew(Facility.class).withNoArguments().thenReturn(chwFacility);
    when(facilityService.getByCode(chwFacility)).thenReturn(chwFacility);
    when(userService.getByUserName(user.getUserName())).thenReturn(user);
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    when(requisitionGroupMemberService.getAllRequisitionGroupMembersByFacility(baseFacility.getId())).
      thenReturn(asList(new RequisitionGroupMember(requisitionGroup, baseFacility)));

    restAgentService.update(agent, user.getId());

    verify(chwFacility).setName(agent.getAgentName());
    verify(chwFacility).setMainPhone(agent.getPhoneNumber());
    verify(chwFacility).setActive(Boolean.parseBoolean(agent.getActive()));
    verify(chwFacility).setParentFacilityId(baseFacility.getId());
    verify(chwFacility).setGeographicZone(baseFacility.getGeographicZone());
    verify(chwFacility).setFacilityType(baseFacility.getFacilityType());
    verify(facilityService).update(chwFacility);
    verify(requisitionGroupMemberService).deleteMembersFor(chwFacility);
    verify(requisitionGroupMemberService).getAllRequisitionGroupMembersByFacility(baseFacility.getId());
    ArgumentCaptor<RequisitionGroupMember> captor = ArgumentCaptor.forClass(RequisitionGroupMember.class);
    verify(requisitionGroupMemberService).save(captor.capture());
  }

  @Test
  public void shouldThrowExceptionIfAgentCodeIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setAgentCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    restAgentService.create(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfAgentNameIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setAgentName(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    restAgentService.create(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityCodeIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setParentFacilityCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    restAgentService.create(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityIsVirtualFacility() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);
    baseFacility.setVirtualFacility(true);
    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.parent.facility.virtual");

    restAgentService.create(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfCHWIsAlreadyRegistered() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    when(facilityService.getByCode(facility)).thenReturn(facility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.agent.already.registered");

    restAgentService.create(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfActiveFieldIsNullOnUpdate() throws Exception {
    Agent agent = make(a(defaultCHW));
    agent.setActive(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    restAgentService.update(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfCHWIsNotVirtualOnUpdate() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility nonVirtualFacility = new Facility();
    nonVirtualFacility.setVirtualFacility(false);
    nonVirtualFacility.setCode(agent.getAgentCode());
    when(facilityService.getByCode(nonVirtualFacility)).thenReturn(nonVirtualFacility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.agent.not.virtual");

    restAgentService.update(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfInvalidAgentCodeOnUpdate() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.agent.code");

    restAgentService.update(agent, user.getId());
  }

  @Test
  public void shouldThrowExceptionIfCHWBeingUpdatedIsDeleted() throws Exception {
    Agent agent = make(a(defaultCHW));
    Facility facility = new Facility();
    facility.setVirtualFacility(true);
    facility.setEnabled(false);
    Facility chwFacility = new Facility();
    whenNew(Facility.class).withNoArguments().thenReturn(chwFacility);
    when(facilityService.getByCode(chwFacility)).thenReturn(facility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.agent.deleted");

    restAgentService.update(agent, user.getId());

  }

  private Facility getBaseFacility(Agent agent) {
    Facility baseFacility = new Facility(1l);
    baseFacility.setCode(agent.getParentFacilityCode());
    baseFacility.setFacilityType(new FacilityType());
    baseFacility.setGeographicZone(new GeographicZone());
    baseFacility.setOperatedBy(new FacilityOperator());
    return baseFacility;
  }

}
