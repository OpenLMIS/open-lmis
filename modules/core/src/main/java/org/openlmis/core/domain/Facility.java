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

    @ImportField(mandatory = true)
    private String code;
    @ImportField(mandatory = true)
    private String name;
    @ImportField
    private String description;
    @ImportField
    private String GLN;
    @ImportField
    private String mainPhone;
    @ImportField
    private String fax;
    @ImportField
    private String address1;
    @ImportField
    private String address2;
    @ImportField(mandatory = true, type = "long")
    private long geographicZone;
    @ImportField(mandatory = true, type = "long")
    private long type;
    @ImportField(type = "long")
    private long catchmentPopulation;
    @ImportField(type = "double")
    private double latitude;
    @ImportField(type = "double")
    private double longitude;
    @ImportField(type = "double")
    private double altitude;
    @ImportField(type = "long")
    private long operatedBy;
    @ImportField (type = "double")
    private double coldStorageGrossCapacity;
    @ImportField (type = "double")
    private double coldStorageNetCapacity;
    @ImportField(type = "boolean")
    private boolean suppliesOthers;
    @ImportField(type = "boolean", mandatory = true, name="is_sdp")
    private boolean sdp;
    @ImportField(type = "boolean")
    private boolean hasElectricity;
    @ImportField(type = "boolean")
    private boolean online;
    @ImportField(type = "boolean")
    private boolean hasElectronicSCC;
    @ImportField(type = "boolean")
    private boolean hasElectronicDAR;
    @ImportField(type = "boolean", mandatory = true)
    private boolean active;
    @ImportField(type = "Date", mandatory = true)
    private Date goLiveDate;
    @ImportField(type = "Date")
    private Date goDownDate;
    @ImportField(type = "boolean")
    private boolean satelliteFacility;
    @ImportField
    private String satelliteParentCode;
    @ImportField
    private String comment;
    @ImportField(type = "boolean")
    private boolean doNotDisplay;
    private String modifiedBy;
    private Date modifiedDate;

    @Override
    public boolean validate() {
        return true;
    }
}
