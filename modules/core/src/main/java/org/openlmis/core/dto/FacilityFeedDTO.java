/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.Facility;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class FacilityFeedDTO extends BaseFeedDTO {


  private String code;

  private String name;

  private String description;

  private String gln;

  private String mainPhone;

  private String fax;

  private String address1;

  private String address2;

  private String geographicZone;

  private String facilityType;

  private Long catchmentPopulation;

  private Double latitude;

  private Double longitude;

  private Double altitude;

  private String operatedBy;

  private Double coldStorageGrossCapacity;

  private Double coldStorageNetCapacity;

  private Boolean suppliesOthers;

  private Boolean sdp;

  private Boolean hasElectricity;

  private Boolean online;

  private Boolean hasElectronicSCC;

  private Boolean hasElectronicDAR;

  private Boolean active;

  private Date goLiveDate;

  private Date goDownDate;

  private Boolean satellite;

  private String parentFacility;

  private String comment;

  private boolean enabled;

  private Date modifiedDate;

  private Boolean virtualFacility;

  public FacilityFeedDTO(Facility facility, Facility parentFacility) {
    this.code = facility.getCode();
    this.name = facility.getName();
    this.facilityType = facility.getFacilityType().getName();
    this.description = facility.getDescription();
    this.gln = facility.getGln();
    this.mainPhone = facility.getMainPhone();
    this.fax = facility.getFax();
    this.address1 = facility.getAddress1();
    this.address2 = facility.getAddress2();
    this.geographicZone = facility.getGeographicZone().getName();
    this.catchmentPopulation = facility.getCatchmentPopulation();
    this.latitude = facility.getLatitude();
    this.longitude = facility.getLongitude();
    this.altitude = facility.getAltitude();
    this.operatedBy = (facility.getOperatedBy() != null) ? facility.getOperatedBy().getText() : null;
    this.coldStorageGrossCapacity = facility.getColdStorageGrossCapacity();
    this.coldStorageNetCapacity = facility.getColdStorageNetCapacity();
    this.sdp = facility.getSdp();
    this.online = facility.getOnline();
    this.suppliesOthers = facility.getSuppliesOthers();
    this.hasElectricity = facility.getHasElectricity();
    this.hasElectronicSCC = facility.getHasElectronicScc();
    this.hasElectronicDAR = facility.getHasElectronicDar();
    this.satellite = facility.getSatellite();
    this.virtualFacility = facility.getVirtualFacility();
    this.active = facility.getActive();
    this.goLiveDate = facility.getGoLiveDate();
    this.goDownDate = facility.getGoDownDate();
    this.parentFacility = parentFacility != null ? parentFacility.getCode() : null;
    this.comment = facility.getComment();
    this.enabled = facility.getEnabled();
    this.modifiedDate = facility.getModifiedDate();
  }

}
