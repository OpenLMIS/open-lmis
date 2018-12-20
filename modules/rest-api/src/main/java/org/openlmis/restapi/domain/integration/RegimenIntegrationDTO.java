package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openlmis.restapi.deserializer.ActiveDeserializer;

public class RegimenIntegrationDTO {

    @JsonProperty(value="description")
    private String name;

    @JsonProperty(value="code")
    private String code;

    @JsonProperty(value="status")
    @JsonDeserialize(using = ActiveDeserializer.class)
    private Boolean active;

    @JsonProperty(value="areaCode")
    private String areaCode;

    @JsonProperty(value="categoryCode")
    private String categoryCode;

    @JsonProperty(value="categoryDescription")
    private String categoryName;

    private Boolean isCustom = false;

}
