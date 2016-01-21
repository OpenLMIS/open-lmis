/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaintenanceRequest extends BaseModel {

  private Long userId;
  private Long facilityId;
  private Long inventoryId;
  private Long vendorId;

  private Date requestDate;
  private String reason;
  private Date recommendedDate;
  private String comment;

  private Boolean resolved;
  private String vendorComment;

  // these variables are for display purposes only
  private String equipmentName;
  private String facilityName;
  private MaintenanceLog maintenanceDetails;
  // end of hack


  public String getRequestedDateString() {
    return getFormattedDate(this.requestDate);
  }

  public String getRecommendedDateString() {
    return getFormattedDate(this.recommendedDate);
  }

}
