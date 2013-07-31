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

  private String type;

  private String description;

  private String GLN;

  private String mainPhone;

  private String fax;

  private String address1;

  private String address2;

  private String geographicZone;

  private Long catchmentPopulation;

  private Double latitude;

  private Double longitude;

  private Double altitude;

  private String operatedBy;

  private Double coldStorageGrossCapacity;

  private Double coldStorageNetCapacity;

  private boolean suppliesOthers;

  private boolean isSDP;

  private boolean hasElectricity;

  private boolean isOnline;

  private boolean hasElectronicSCC;

  private boolean hasElectronicDAR;

  private boolean active;

  private Date goLiveDate;

  private Date goDownDate;

  private boolean satelliteFacility;

  private boolean virtualFacility;

  private String parentFacility;

  private String comments;

  private boolean doNotDisplay;

  private Date modifiedDate;

  public FacilityFeedDTO(Facility facility, Facility parentFacility) {
    this.code = facility.getCode();
    this.name = facility.getName();
    this.type = facility.getFacilityType().getName();
    this.description = facility.getDescription();
    this.GLN = facility.getGln();
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
    this.isSDP = facility.getSdp();
    this.isOnline = (facility.getOnline() != null) ? facility.getOnline() : false;
    this.suppliesOthers = (facility.getSuppliesOthers() != null) ? facility.getSuppliesOthers() : false;
    this.hasElectricity = (facility.getHasElectricity() != null) ? facility.getHasElectricity() : false;
    this.hasElectronicSCC = (facility.getHasElectronicScc() != null) ? facility.getHasElectronicScc() : false;
    this.hasElectronicDAR = (facility.getHasElectronicDar() != null) ? facility.getHasElectronicDar() : false;
    this.satelliteFacility = (facility.getSatellite() != null) ? facility.getSatellite() : false;
    this.active = facility.getActive();
    this.goLiveDate = facility.getGoLiveDate();
    this.goDownDate = facility.getGoDownDate();
    this.parentFacility = parentFacility != null ? parentFacility.getCode() : null;
    this.virtualFacility = (facility.getVirtualFacility() != null) ? facility.getVirtualFacility() : false;
    this.satelliteFacility = (facility.getSatellite() != null) ? facility.getSatellite() : false;
    this.comments = facility.getComment();
    this.doNotDisplay = facility.getDataReportable();
    this.modifiedDate = facility.getModifiedDate();
  }

}
