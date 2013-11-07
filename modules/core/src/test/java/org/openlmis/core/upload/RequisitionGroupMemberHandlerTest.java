/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionGroupMemberHandlerTest {

  public static final Integer USER = 1;

  @Mock
  RequisitionGroupMemberService requisitionGroupMemberService;

  @Mock
  FacilityService facilityService;


  @InjectMocks
  RequisitionGroupMemberHandler requisitionGroupMemberHandler;

  @Test
  public void shouldSaveRGMembersTaggedWithModifiedBy() throws Exception {
    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();

    requisitionGroupMemberHandler.save(requisitionGroupMember);

    verify(requisitionGroupMemberService).save(requisitionGroupMember);
  }

  @Test
  public void shouldUpdateVirtualFacilitiesInPostProcess() throws Exception {
    Date modifiedDate = new Date();
    AuditFields auditFields = new AuditFields(modifiedDate);
    Facility facility = mock(Facility.class);
    List<Facility> facilities = asList(facility);
    when(facilityService.getAllByRequisitionGroupMemberModifiedDate(modifiedDate)).thenReturn(facilities);

    requisitionGroupMemberHandler.postProcess(auditFields);

    verify(requisitionGroupMemberService).updateMembersForVirtualFacilities(facility);
  }
}
