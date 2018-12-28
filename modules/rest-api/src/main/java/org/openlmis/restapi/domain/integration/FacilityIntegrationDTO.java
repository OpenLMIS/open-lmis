package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.restapi.deserializer.ActiveDeserializer;
import org.openlmis.restapi.deserializer.DateTimeDeserializer;

import java.util.Date;
import java.util.List;

@Data
public class FacilityIntegrationDTO {

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "phone")
    private String mainPhone;

    @JsonProperty(value = "fax")
    private String fax;

    @JsonProperty(value = "address")
    private String address1;

    @JsonProperty(value = "latitude")
    private Double latitude;

    @JsonProperty(value = "longitude")
    private Double longitude;

    @JsonProperty(value = "districtDescription")
    private String geographicZoneName;

    @JsonProperty(value = "clientTypeDescription")
    private String facilityTypeName;

    @JsonIgnore
    private Boolean sdp = false;

    @JsonProperty(value="status")
    @JsonDeserialize(using = ActiveDeserializer.class)
    private Boolean active;

    @JsonProperty(value = "startDate")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date goLiveDate;

    @JsonIgnore
    private Boolean enabled = true;

    @JsonProperty(value = "areas")
    List<ProgramSupportedIntegrationDTO> programsSupported;
}
