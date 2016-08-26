package org.openlmis.stockmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class LotEvent {

    private String lotNumber;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using=DateDeserializer.class)
    private Date expirationDate;

    private Long quantity;

    private Map<String, String> customProps;

    public LotEvent(String lotNumber, Date expirationDate, long quantity) {
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }
}
