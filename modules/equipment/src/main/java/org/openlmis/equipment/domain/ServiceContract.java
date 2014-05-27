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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.equipment.dto.ContractDetail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceContract extends BaseModel{

  private Long vendorId;
  private String identifier;
  private Date startDate;
  private Date endDate;
  private String description;
  private String terms;
  private String coverage;
  private Date contractDate;

  private List<ContractDetail> facilities;
  private List<ContractDetail> serviceTypes;
  private List<ContractDetail> equipments;

  private String formatDate(Date date){
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
      return date == null ? null : simpleDateFormat.format(date);
    }catch(Exception exp){

    }
    return null;
  }

  public String getStartDateString()  {
    return formatDate(this.startDate);
  }

  public String getEndDateString()  {
    return formatDate(this.endDate);
  }

  public String getContractDateString()  {
    return formatDate(this.contractDate);
  }



}
