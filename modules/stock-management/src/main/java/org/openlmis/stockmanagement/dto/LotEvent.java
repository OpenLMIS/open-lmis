package org.openlmis.stockmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.Map;

public class LotEvent {

    private String lotNumber;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using=DateDeserializer.class)
    private Date expirationDate;

    private Long quantity;

    private Map<String, String> customProps;
}
