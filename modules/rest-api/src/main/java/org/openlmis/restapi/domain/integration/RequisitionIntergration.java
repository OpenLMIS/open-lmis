package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;
import java.util.List;

@Data
public class RequisitionIntergration {

    private String facilityCode;

    private Integer requisitionId;

    private String requisitionYear;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date RequisitionDate;

    private Boolean isEmergency;

    private String facilityName;

    private String requistionStatus;

    private List<RequisitionLineItemIntergration> products;

    private List<RegimenLineItemIntergration> regimens;
}