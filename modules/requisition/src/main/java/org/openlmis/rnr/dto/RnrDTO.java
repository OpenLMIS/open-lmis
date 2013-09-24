/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class RnrDTO {

  private Long id;
  private String programName;
  private Long programId;
  private String facilityName;
  private String facilityCode;
  private boolean emergency;
  private Date submittedDate;
  private Date modifiedDate;
  private Date periodStartDate;
  private Date periodEndDate;
  private String periodName;
  private Long facilityId;
  private String supplyingDepotName;
  private String status;
  private Long modifiedBy;

  public static List<RnrDTO> prepareForListApproval(List<Rnr> requisitions) {
    List<RnrDTO> result = new ArrayList<>();
    for (Rnr requisition : requisitions) {
      result.add(prepareDTOWithSupplyingDepot(requisition));
    }
    return result;
  }

  public static List<RnrDTO> prepareForView(List<Rnr> requisitions) {
    List<RnrDTO> result = new ArrayList<>();
    for (Rnr requisition : requisitions) {
      RnrDTO rnrDTO = populateDTOWithRequisition(requisition);
      rnrDTO.status = requisition.getStatus().name();
      result.add(rnrDTO);
    }
    return result;
  }

  public static RnrDTO prepareForOrderView(Rnr requisition) {
    RnrDTO rnrDTO = prepareDTOWithSupplyingDepot(requisition);
    rnrDTO.setPeriodName(requisition.getPeriod().getName());
    return rnrDTO;
  }

  private static RnrDTO prepareDTOWithSupplyingDepot(Rnr requisition) {
    RnrDTO rnrDTO = populateDTOWithRequisition(requisition);
    if (requisition.getSupplyingDepot() != null) {
      rnrDTO.supplyingDepotName = requisition.getSupplyingDepot().getName();
    }
    return rnrDTO;
  }

  private static RnrDTO populateDTOWithRequisition(Rnr requisition) {
    RnrDTO rnrDTO = new RnrDTO();
    rnrDTO.id = requisition.getId();
    rnrDTO.programId = requisition.getProgram().getId();
    rnrDTO.facilityId = requisition.getFacility().getId();
    rnrDTO.programName = requisition.getProgram().getName();
    rnrDTO.facilityCode = requisition.getFacility().getCode();
    rnrDTO.facilityName = requisition.getFacility().getName();
    rnrDTO.submittedDate = requisition.getSubmittedDate();
    rnrDTO.modifiedDate = requisition.getModifiedDate();
    rnrDTO.periodStartDate = requisition.getPeriod().getStartDate();
    rnrDTO.periodEndDate = requisition.getPeriod().getEndDate();
    rnrDTO.setEmergency(requisition.getEmergency());
    return rnrDTO;
  }
}
