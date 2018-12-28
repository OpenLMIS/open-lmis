package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.restapi.deserializer.ActiveDeserializer;

import java.util.Date;

@Data
public class ProgramSupportedIntegrationDTO {


    private Long facilityId;

    @JsonProperty(value = "areaCode")
    private String programCode;

    private Date startDate;

    @JsonProperty(value = "status")
    @JsonDeserialize(using = ActiveDeserializer.class)
    private boolean active;

}
