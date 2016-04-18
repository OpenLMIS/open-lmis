package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class ProgramProductResponse {
    private String programCode;
    private String productCode;
    private boolean isActive;
    private String category;
}
