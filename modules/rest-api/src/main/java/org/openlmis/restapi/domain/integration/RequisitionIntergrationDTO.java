package org.openlmis.restapi.domain.integration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;
import java.util.List;

@Data
public class
RequisitionIntergrationDTO {

    private String facilityCode;

    private Integer requisitionId;

    private String requisitionType;

    private String requisitionPeriodCode = null;

    private String requisitionYear;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date requisitionDate;

    private String natureZa;

    private String facilityName;

    private Integer quantityOfProducts;

    private Integer quantityOfRegimens;

    private List<RequisitionLineItemIntergration> products;

    private List<RegimenLineItemIntergration> regimens;

    public void prepareDataForFC(final RequisitionIntergration requisitionIntergration) {
        facilityCode = requisitionIntergration.getFacilityCode();
        requisitionId = requisitionIntergration.getRequisitionId();
        requisitionYear = requisitionIntergration.getRequisitionYear();
        requisitionDate = requisitionIntergration.getRequisitionDate();
        facilityName = requisitionIntergration.getFacilityName();
        products = requisitionIntergration.getProducts();
        regimens = requisitionIntergration.getRegimens();

        quantityOfProducts = requisitionIntergration.getProducts().size();
        quantityOfRegimens = requisitionIntergration.getRegimens().size();

        natureZa = requisitionIntergration.getRequistionStatus();

        requisitionType = requisitionIntergration.getRequistionStatus().equals("NORMAL") ? "P" : "N";
    }
}