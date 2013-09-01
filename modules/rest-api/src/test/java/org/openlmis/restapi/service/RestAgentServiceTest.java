/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.VendorService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.Principal;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.AgentBuilder.defaultCHW;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest(RestAgentService.class)
public class RestAgentServiceTest {

  @Mock
  FacilityService facilityService;

  @InjectMocks
  RestAgentService restAgentService;

  @Mock
  private VendorService vendorService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
  }

  @Test
  public void shouldCreateFacilityForCHW() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);

    Facility facility = mock(Facility.class);
    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    when(vendorService.getByName(principal.getName())).thenReturn(new Vendor());
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    restAgentService.create(agent, principal.getName());

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
    verify(facility).setOperatedBy(baseFacility.getOperatedBy());
    verify(facility).setGoLiveDate(currentTimeStamp);
    verify(facilityService).save(facility);
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
    whenNew(Facility.class).withNoArguments().thenReturn(chwFacility);
    when(facilityService.getByCode(chwFacility)).thenReturn(chwFacility);
    when(vendorService.getByName(principal.getName())).thenReturn(new Vendor());

    restAgentService.update(agent, principal.getName());

    verify(chwFacility).setName(agent.getAgentName());
    verify(chwFacility).setMainPhone(agent.getPhoneNumber());
    verify(chwFacility).setActive(Boolean.parseBoolean(agent.getActive()));
    verify(chwFacility).setParentFacilityId(baseFacility.getId());
    verify(chwFacility).setGeographicZone(baseFacility.getGeographicZone());
    verify(chwFacility).setFacilityType(baseFacility.getFacilityType());
    verify(chwFacility).setOperatedBy(baseFacility.getOperatedBy());
    verify(facilityService).update(chwFacility);
  }

  @Test
  public void shouldThrowExceptionIfAgentCodeIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setAgentCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restAgentService.create(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfAgentNameIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setAgentName(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restAgentService.create(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityCodeIsMissing() throws Exception {

    Agent agent = make(a(defaultCHW));
    agent.setParentFacilityCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restAgentService.create(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityIsVirtualFacility() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(agent);
    baseFacility.setVirtualFacility(true);
    when(facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode())).thenReturn(baseFacility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.parent.facility.virtual");

    restAgentService.create(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfCHWIsAlreadyRegistered() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    when(facilityService.getByCode(facility)).thenReturn(facility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.agent.already.registered");

    restAgentService.create(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfActiveFieldIsNullOnUpdate() throws Exception {
    Agent agent = make(a(defaultCHW));
    agent.setActive(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restAgentService.update(agent, principal.getName());
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

    restAgentService.update(agent, principal.getName());
  }

  @Test
  public void shouldThrowExceptionIfInvalidAgentCodeOnUpdate() throws Exception {
    Agent agent = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.agent.code");

    restAgentService.update(agent, principal.getName());
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

    restAgentService.update(agent, principal.getName());

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
