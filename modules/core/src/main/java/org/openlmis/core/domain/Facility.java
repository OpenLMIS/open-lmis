package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Facility implements Importable, BaseModel {

  private Integer id;

  @ImportField(mandatory = true, name = "Facility Code")
  private String code;

  @ImportField(mandatory = true, name = "Facility Name")
  private String name;

  @ImportField(name = "Facility Description")
  private String description;

  @ImportField(name = "GLN")
  private String gln;

  @ImportField(name = "Facility Main Phone")
  private String mainPhone;

  @ImportField(name = "Facility Fax")
  private String fax;

  @ImportField(name = "Facility Address1")
  private String address1;

  @ImportField(name = "Facility Address2")
  private String address2;

  @ImportField(mandatory = true, type = "long", name = "Geographic Zone ID", nested = "id")
  private GeographicZone geographicZone;

  @ImportField(mandatory = true, name = "Facility Type Code", nested = "code")
  private FacilityType facilityType;

  @ImportField(type = "long", name = "Catchment Population")
  private Long catchmentPopulation;

  @ImportField(type = "double", name = "Facility LAT")
  private Double latitude;

  @ImportField(type = "double", name = "Facility LONG")
  private Double longitude;

  @ImportField(type = "double", name = "Facility Altitude")
  private Double altitude;

  @ImportField(type = "String", name = "Facility Operated By", nested = "code")
  private FacilityOperator operatedBy;

  @ImportField(type = "double", name = "Cold Storage Gross Capacity")
  private Double coldStorageGrossCapacity;

  @ImportField(type = "double", name = "Cold Storage Net Capacity")
  private Double coldStorageNetCapacity;

  @ImportField(type = "boolean", name = "Facility Supplies Others")
  private Boolean suppliesOthers;

  @ImportField(type = "boolean", mandatory = true, name = "Facility Is SDP")
  private Boolean sdp;

  @ImportField(type = "boolean", name = "Facility Has Electricity")
  private Boolean hasElectricity;

  @ImportField(type = "boolean", name = "Facility Is Online")
  private Boolean online;

  @ImportField(type = "boolean", name = "Facility Has Electronic SCC")
  private Boolean hasElectronicScc;

  @ImportField(type = "boolean", name = "Facility Has Electronic DAR")
  private Boolean hasElectronicDar;

  @ImportField(type = "boolean", mandatory = true, name = "Is Active")
  private Boolean active;

  @ImportField(type = "Date", mandatory = true, name = "Facility Go Live Date")
  private Date goLiveDate;

  @ImportField(type = "Date", name = "Facility Go Down Date")
  private Date goDownDate;

  @ImportField(type = "boolean", name = "Is Satellite Facility")
  private Boolean satellite;

  @ImportField(name = "Satellite Parent ID")
  private String satelliteParentCode;

  @ImportField(name = "Facility Comments")
  private String comment;

  @ImportField(type = "boolean", mandatory = true, name = "Data Reportable")
  private Boolean dataReportable;

  List<ProgramSupported> supportedPrograms = new ArrayList<>();

  private String modifiedBy;

  private Date modifiedDate;

  public Facility(Integer id) {
    this.id = id;
  }

  public Facility(Integer id, String code, String name, FacilityOperator operatedBy, GeographicZone geographicZone, FacilityType facilityType) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.operatedBy = operatedBy;
    this.geographicZone = geographicZone;
    this.facilityType = facilityType;
  }

  public Facility basicInformation() {
    return new Facility(id, code, name, operatedBy, geographicZone, facilityType);
  }
}
