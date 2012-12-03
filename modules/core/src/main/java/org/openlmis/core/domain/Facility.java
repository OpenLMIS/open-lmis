package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility implements Importable {

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

    @ImportField(mandatory = true, type = "long", name = "Geographic Zone ID")
    private long geographicZone;

    @ImportField(mandatory = true, name = "Facility Type ID")
    private String facilityTypeCode;

    @ImportField(type = "long", name = "Catchment Population")
    private long catchmentPopulation;

    @ImportField(type = "double", name = "Facility LAT")
    private double latitude;

    @ImportField(type = "double", name = "Facility LONG")
    private double longitude;

    @ImportField(type = "double", name = "Facility Altitude")
    private double altitude;

    @ImportField(type = "String", name = "Facility Operated By")
    private String operatedBy;

    @ImportField(type = "double", name = "Cold Storage Gross Capacity")
    private double coldStorageGrossCapacity;

    @ImportField(type = "double", name = "Cold Storage Net Capacity")
    private double coldStorageNetCapacity;

    @ImportField(type = "boolean", name = "Facility Supplies Others")
    private boolean suppliesOthers;

    @ImportField(type = "boolean", mandatory = true, name = "Facility Is SDP")
    private boolean sdp;

    @ImportField(type = "boolean", name = "Facility Has Electricity")
    private boolean hasElectricity;

    @ImportField(type = "boolean", name = "Facility Is Online")
    private boolean online;

    @ImportField(type = "boolean", name = "Facility Has Electronic SCC")
    private boolean hasElectronicScc;

    @ImportField(type = "boolean", name = "Facility Has Electronic DAR")
    private boolean hasElectronicDar;

    @ImportField(type = "boolean", mandatory = true, name = "Is Active")
    private boolean active;

    @ImportField(type = "Date", mandatory = true, name = "Facility Go Live Date")
    private Date goLiveDate;

    @ImportField(type = "Date", name = "Facility Go Down Date")
    private Date goDownDate;

    @ImportField(type = "boolean", name = "Is Satellite Facility")
    private boolean satellite;

    @ImportField(name = "Satellite Parent ID")
    private String satelliteParentCode;

    @ImportField(name = "Facility Comments")
    private String comment;

    @ImportField(type = "boolean", name = "Data Reportable")
    private boolean dataReportable;

    List<Program> supportedPrograms = new ArrayList<>();

    private String modifiedBy;

    private Date modifiedDate;
}
