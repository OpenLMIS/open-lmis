/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.DateSerializer;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EquipmentInventory extends BaseModel {

  private Long facilityId;
  private Long programId;
  private Long equipmentId;

  private Equipment equipment;

  private Long operationalStatusId;
  private String serialNumber;
  private String manufacturerName;
  private String model;
  private String energySource;
  private Integer yearOfInstallation;
  private Float purchasePrice;
  private String sourceOfFund;
  private Boolean replacementRecommended;
  private String reasonForReplacement;
  private String nameOfAssessor;

  @JsonSerialize(using = DateSerializer.class)
  private Date dateLastAssessed;

//  public String getDateLastAssessedString()  {
//    try {
//      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");
//      return this.dateLastAssessed == null ? null : simpleDateFormat.format(this.dateLastAssessed);
//    }catch(Exception exp){
//
//    }
//    return null;
//  }


}
