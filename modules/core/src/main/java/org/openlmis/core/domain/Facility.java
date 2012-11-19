package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility implements Importable {

    @ImportField(mandatory = true, name="FacilityCode")
    private String code;
    @ImportField(mandatory = true, name="FacilityName")
    private String name;
    @ImportField(name = "FacilityDescription")
    private String description;
    @ImportField
    private String GLN;
    @ImportField(name = "FacilityMainPhone")
    private String mainPhone;
    @ImportField(name = "FacilityFax")
    private String fax;
    @ImportField(name = "FacilityAddress1")
    private String address1;
    @ImportField(name = "FacilityAddress2")
    private String address2;
    @ImportField(mandatory = true, type = "long", name="GeographicZoneID")
    private long geographicZone;
    @ImportField(mandatory = true, type = "long", name = "FacilityTypeID")
    private long type;
    @ImportField(type = "long" )
    private long catchmentPopulation;
    @ImportField(type = "double", name = "FacilityLAT")
    private double latitude;
    @ImportField(type = "double", name = "FacilityLONG")
    private double longitude;
    @ImportField(type = "double", name = "FacilityAltitude")
    private double altitude;
    @ImportField(type = "long", name = "FacilityOperatedBy")
    private Long operatedBy;
    @ImportField (type = "double")
    private double coldStorageGrossCapacity;
    @ImportField (type = "double")
    private double coldStorageNetCapacity;
    @ImportField(type = "boolean", name = "FacilitySuppliesOthers")
    private boolean suppliesOthers;
    @ImportField(type = "boolean", mandatory = true, name="FacilityIsSDP")
    private boolean sdp;
    @ImportField(type = "boolean", name="FacilityIsOnline")
    private boolean online;
    @ImportField(type = "boolean", name="FacilityHasElectricity")
    private boolean hasElectricity;
    @ImportField(type = "boolean", name="FacilityHasElectronicSCC")
    private boolean hasElectronicScc;
    @ImportField(type = "boolean", name="FacilityHasElectronicDAR")
    private boolean hasElectronicDar;
    @ImportField(type = "boolean", mandatory = true, name="IsActive")
    private boolean active;
    @ImportField(type = "Date", mandatory = true, name="FacilityGoLiveDate")
    private Date goLiveDate;
    @ImportField(type = "Date", name = "FacilityGoDownDate")
    private Date goDownDate;
    @ImportField(type = "boolean", name = "IsSatelliteFacility")
    private boolean satellite;
    @ImportField(name = "SatelliteParentCode")
    private String satelliteParentCode;
    @ImportField(name = "FacilityComments")
    private String comment;
    @ImportField(type = "boolean")
    private boolean doNotDisplay;
    private String modifiedBy;
    private Date modifiedDate;
}
