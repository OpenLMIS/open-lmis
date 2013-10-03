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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.dto.BaseFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;

import java.io.IOException;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RnrFeedDTO extends BaseFeedDTO {
  private Long requisitionId;
  private Long facilityId;
  private Long programId;
  private Long periodId;
  private RnrStatus requisitionStatus;
  private String externalSystem;

  public static RnrFeedDTO populate(Rnr rnr, Vendor vendor) {
    return new RnrFeedDTO(rnr.getId(), rnr.getFacility().getId(), rnr.getProgram().getId(), rnr.getPeriod().getId(), rnr.getStatus(), vendor.getName());
  }


}
