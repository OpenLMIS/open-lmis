/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.rnr.domain.Rnr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * DTO for Rnr. It is a client side representation of Rnr with its attributes.
 */

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class RnrDTO {

  private Long id;
  private String programName;
  private String programCode;
  private Long programId;
  private String facilityName;
  private String facilityCode;
  private String agentCode;
  private boolean emergency;
  private Date submittedDate;
  private Date modifiedDate;
  private Date periodStartDate;
  private Date periodEndDate;
  private String stringSubmittedDate;
  private String stringModifiedDate;
  private String stringPeriodStartDate;
  private String stringPeriodEndDate;
  private String periodName;
  private Long facilityId;
  private String supplyingDepotName;
  private List<RnrLineItemDTO> products;
  private String requisitionStatus;
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
      rnrDTO.requisitionStatus = requisition.getStatus().name();
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

    rnrDTO.stringSubmittedDate = formatDate(requisition.getSubmittedDate());
    rnrDTO.stringModifiedDate = formatDate(requisition.getModifiedDate());
    rnrDTO.stringPeriodStartDate = formatDate(requisition.getPeriod().getStartDate());
    rnrDTO.stringPeriodEndDate = formatDate(requisition.getPeriod().getEndDate());

    rnrDTO.emergency = requisition.isEmergency();
    return rnrDTO;
  }

  private static String formatDate(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return date == null ? null : simpleDateFormat.format(date);
  }
}
