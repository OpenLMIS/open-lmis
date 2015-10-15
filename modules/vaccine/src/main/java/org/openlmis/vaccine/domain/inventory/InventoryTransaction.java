package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryTransaction extends BaseModel {

    Long facilityId;
    Long toFacilityId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
    @JsonDeserialize(using=DateDeserializer.class)
    Date initiatedDate;

    Long productId;

    Product product;

    Long quantity;

    String notes;

    List<LotOnHandTransaction> lots;

    //For stock Movement line Item
    private String issueVoucher;
    private String issueDate;
    private Long dosesRequested;
    private Long gap;
    private String toFacilityName;
    private Long productCategoryId;
}