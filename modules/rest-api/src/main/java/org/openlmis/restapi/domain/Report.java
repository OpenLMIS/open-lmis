/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Report represents an entity which holds RnrLineItem list, RegimenLineItem list along with additional meta attributes
 * to represent Rnr. The purpose for having an additional object is to restrict/validate unwanted attributes
 * in API request,also include some validations on data sent in request.
 */

@Data
@NoArgsConstructor
public class Report {

  private List<RnrLineItem> products;
  private List<RnrLineItem> nonFullSupplyProducts;
  private List<RegimenLineItem> regimens;
  private String agentCode;
  private String programCode;
  private String approverName;

  // added for the sdp version
  private Long periodId;
  private Boolean emergency;

  private List<PatientQuantificationLineItem> patientQuantifications;

  public void validate() {
    if (isEmpty(agentCode) || isEmpty(programCode)) {
      throw new DataException("error.mandatory.fields.missing");
    }
  }

  @JsonIgnore
  public Rnr getRequisition(Long requisitionId, Long modifiedBy) {
    Rnr requisition = new Rnr(requisitionId);
    requisition.setModifiedBy(modifiedBy);
    requisition.setFullSupplyLineItems(products);

    return requisition;
  }

  public void validateForApproval() {
    if (products == null || isEmpty(approverName)) {
      throw new DataException("error.mandatory.fields.missing");
    }
    for (RnrLineItem rnrLineItem : products) {
      if (isEmpty(rnrLineItem.getProductCode()) || rnrLineItem.getQuantityApproved() == null)
        throw new DataException("error.mandatory.fields.missing");
      if (rnrLineItem.getQuantityApproved() < 0)
        throw new DataException("error.restapi.quantity.approved.negative");
    }
  }
}
