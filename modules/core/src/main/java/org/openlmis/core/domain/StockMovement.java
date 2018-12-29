package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;

@Data
public class StockMovement {
    private String facilityCode;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date occurred;

    private Integer movementId;

    private String productCode;

    private String category;

    private Integer quantity;

    private String lotNumber;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date expirationDate;

    private String referenceNumber;

    private String productFullName;

    private String FacilityName;

    private Integer packSize;

    private String notes;

    private String adjustmentType;
}