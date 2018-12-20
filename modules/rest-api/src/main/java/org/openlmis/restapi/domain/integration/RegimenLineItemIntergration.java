package org.openlmis.restapi.domain.integration;

import lombok.Data;

@Data
public class RegimenLineItemIntergration {

    private String facilityCode;

    private Integer requisitionId;

    private String programCode;

    private String regimeCode;

    private Integer patientsOnTreatment;
}