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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RequisitionGroupMemberHandler is used for uploads of RequisitionGroup members (facilities).
 * It uploads each RequisitionGroupMember record by record.
 */
@NoArgsConstructor
@Component
public class RequisitionGroupMemberHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private RequisitionGroupMemberService requisitionGroupMemberService;

  @Autowired
  private FacilityService facilityService;


  @Override
  protected BaseModel getExisting(BaseModel record) {
    return requisitionGroupMemberService.getExisting((RequisitionGroupMember) record);
  }

  @Override
  protected void save(BaseModel record) {
    requisitionGroupMemberService.save((RequisitionGroupMember) record);
  }

  @Override
  public void postProcess(AuditFields auditFields) {
    List<Facility> facilities = facilityService.getAllByRequisitionGroupMemberModifiedDate(auditFields.getCurrentTimestamp());
    for (Facility facility : facilities) {
      requisitionGroupMemberService.updateMembersForVirtualFacilities(facility);
    }
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.requisition.group.member";
  }

}
