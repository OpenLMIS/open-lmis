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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentInventory extends BaseModel {

  private Long facilityId;
  private Long programId;
  private Long equipmentId;

  private Equipment equipment;
  private Facility facility;

  private Long operationalStatusId;
  private Long notFunctionalStatusId;
  private String serialNumber;
  private Integer yearOfInstallation;
  private Float purchasePrice;
  private String sourceOfFund;
  private Boolean replacementRecommended;
  private String reasonForReplacement;
  private String nameOfAssessor;
  private Long primaryDonorId;
  private Boolean isActive;
  private Date dateDecommissioned;
  private Date dateLastAssessed;
  private Boolean hasStabilizer;

  public String getDateLastAssessedString() {
    return DateUtil.getFormattedDate(this.dateLastAssessed, "yyyy-MM-dd");
  }

  public String getDateDecommissionedString() {
    return DateUtil.getFormattedDate(this.dateDecommissioned, "yyyy-MM-dd");
  }

}
