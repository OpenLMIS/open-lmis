package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.restapi.deserializer.ActiveDeserializer;

import java.util.List;

@Data
public class ProductIntegrationDTO {

    @JsonProperty(value = "fnm")
    private String code;

    @JsonProperty(value = "fullDescription")
    private String primaryName;

    @JsonProperty(value="status")
    @JsonDeserialize(using = ActiveDeserializer.class)
    private Boolean active;

    @JsonProperty(value = "areas")
    private List<ProductSupportedProgramDTO> productSupportedProgramDTOS;

    @JsonIgnore
    private Long formId = null;

    @JsonIgnore
    private String dispensingUnit = "1";

    @JsonIgnore
    private Integer dosesPerDispensingUnit = 1;

    @JsonIgnore
    private Integer packSize = 1;

    @JsonIgnore
    private Boolean fullSupply = true;

    @JsonIgnore
    private Boolean tracer = false;

    @JsonIgnore
    private Boolean roundToZero = false;

    @JsonIgnore
    private Integer packRoundingThreshold = 1;

    @JsonIgnore
    private Boolean nos = true;

    @JsonIgnore
    private Boolean isBasic = false;

    @JsonIgnore
    private Boolean isKits = false;

    @JsonIgnore
    private Boolean isHiv = false;
}