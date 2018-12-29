package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;

@Data
public class StockMovementDTO {

    private String facilityCode;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date occurred;

    private Integer movementId;

    private String productCode;

    private String category;

    private Integer quantity;

    private Double price = null;

    private String lotNumber;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date expirationDate;

    private String referenceNumber;

    private String adjustmentType;

    private String productFullName;

    private String FacilityName;

    private String gr = null;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date dtemissaogr = null;

    private Integer packSize;

    private String notes;
}