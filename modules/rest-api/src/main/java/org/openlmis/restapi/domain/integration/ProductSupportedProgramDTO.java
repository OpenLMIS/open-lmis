package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductSupportedProgramDTO {

    @JsonProperty("areaCode")
    private String programCode;
}