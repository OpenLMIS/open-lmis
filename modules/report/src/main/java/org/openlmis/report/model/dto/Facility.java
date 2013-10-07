/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper=false)
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
