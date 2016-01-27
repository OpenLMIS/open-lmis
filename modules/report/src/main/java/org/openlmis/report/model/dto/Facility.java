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

package org.openlmis.report.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Facility {

  private Integer id;
  private String code;
  private String name;
  private String description;
  private String gln;
  private String mainPhone;
  private String fax;
  private String address1;
  private String address2;
  private Integer geographicZoneId;
  private Integer typeId;
  private Long catchmentPopulation;
  private Double latitude;
  private Double longitude;
  private Double altitude;
  private Integer operatedById;
  private Double coldStorageGrossCapacity;
  private Double coldStorageNetCapacity;
  private Boolean suppliesOthers;
  private Boolean sdp;
  private Boolean hasElectricity;
  private Boolean online;
  private Boolean hasElectronicScc;
  private Boolean hasElectronicDar;
  private Boolean active;
  private Date goLiveDate;
  private Date goDownDate;
  private Boolean satellite;
  private Integer satelliteParentId;
  private String comment;
  private Boolean dataReportable;
}
