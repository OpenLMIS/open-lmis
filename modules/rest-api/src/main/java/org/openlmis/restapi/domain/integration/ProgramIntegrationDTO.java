package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.restapi.deserializer.ActiveDeserializer;

@Data
public class ProgramIntegrationDTO {

    @JsonProperty(value="code")
    private String code;

    @JsonIgnore
    private String name;

    @JsonProperty(value="description")
    private String description;

    @JsonProperty(value="status")
    @JsonDeserialize(using = ActiveDeserializer.class)
    private Boolean active;

    @JsonIgnore
    private Boolean templateConfigured = false;

    @JsonIgnore
    private Boolean regimenTemplateConfigured = false;

    @JsonIgnore
    private Boolean push = false;

    @JsonIgnore
    private Boolean budgetingApplies = false;


    public void setDescription(String description) {
        this.description = description;
        this.setName(description);
    }
}
