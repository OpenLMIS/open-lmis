package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.openlmis.core.serializer.DateUTCDeserializer;

import java.util.Date;

@Data
public class Soh {

    private String facilityCode;

    private String facilityName;

    private String productCode;

    private String productFullName;

    private int packSize;

    private String lotNumber;

    private Date expirationDate;

    private Long quantityOnHand;

    private Date effectiveDate;

    private Date modifiedDate;
}
