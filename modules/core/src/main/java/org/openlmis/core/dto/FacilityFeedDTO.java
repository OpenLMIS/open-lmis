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

  private Boolean suppliesOthers;

  private Boolean sdp;

  private Boolean hasElectricity;

  private Boolean online;

  private Boolean hasElectronicSCC;

  private Boolean hasElectronicDAR;

  private Boolean active;

  private Date goLiveDate;

  private Date goDownDate;

  private Boolean satelliteFacility;

  private Boolean virtualFacility;

  private String parentFacility;

  private String comments;

  private boolean enabled;

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
    this.sdp = facility.getSdp();
    this.online = facility.getOnline();
    this.suppliesOthers = facility.getSuppliesOthers();
    this.hasElectricity = facility.getHasElectricity();
    this.hasElectronicSCC = facility.getHasElectronicScc();
    this.hasElectronicDAR = facility.getHasElectronicDar();
    this.satelliteFacility = facility.getSatellite();
    this.virtualFacility = facility.getVirtualFacility();
    this.active = facility.getActive();
    this.goLiveDate = facility.getGoLiveDate();
    this.goDownDate = facility.getGoDownDate();
    this.parentFacility = parentFacility != null ? parentFacility.getCode() : null;
    this.comments = facility.getComment();
    this.enabled = facility.getEnabled();
    this.modifiedDate = facility.getModifiedDate();
  }

}
