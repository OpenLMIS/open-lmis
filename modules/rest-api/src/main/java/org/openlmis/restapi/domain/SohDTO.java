package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;

@Data
public class SohDTO {

    private String facilityCode;

    private String facilityName;

    private String productCode;

    private String productFullName;

    private int packSize;

    private String lotNumber;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date expirationDate;

    private Long quantityOnHand;

    @JsonDeserialize(using = DateUTCDeserializer.class)
    private Date effectiveDate;
}
