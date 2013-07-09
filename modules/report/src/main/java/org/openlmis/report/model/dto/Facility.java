package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/29/13
 * Time: 4:10 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
