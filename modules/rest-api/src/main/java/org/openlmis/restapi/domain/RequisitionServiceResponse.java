package org.openlmis.restapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionServiceResponse {

    private String code;
    private String name;
    private String programCode;
    private Boolean active;
}