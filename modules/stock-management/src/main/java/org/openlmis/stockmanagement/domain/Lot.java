package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;

/**
 * Lot represents a product-batch, with a specific manufacturer, manufacture date, etc.
 */
@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Lot extends BaseModel
{
    private Product product;

    private String lotCode;

    private String manufacturerName;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using=DateDeserializer.class)
    private Date manufactureDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using=DateDeserializer.class)
    private Date expirationDate;

    public final boolean isValid() {
        return (null != lotCode && !lotCode.equals("") &&
            null != manufacturerName && !manufacturerName.equals("") &&
            null != expirationDate);
    }
}
