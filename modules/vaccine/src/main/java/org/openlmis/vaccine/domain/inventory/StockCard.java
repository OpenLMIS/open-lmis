package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ISA;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockCard extends BaseModel {


    Double whoRatio;
    Integer dosesPerYear;
    Double wastageFactor;
    Double bufferPercentage;
    Integer minimumValue;
    Integer maximumValue;
    Integer adjustmentValue;

    @JsonIgnore
    Facility facility;

    Long facilityId;
    Long toFacilityId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using=DateDeserializer.class)
    Date initiatedDate;

    Long productId;

    Product product;

    Long totalQuantityOnHand;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = DateDeserializer.class)
    Date effectiveDate;

    String notes;

    List<StockCardEntry> entries;

    List<LotOnHand> lotsOnHand;






    /*
     The single overriddenisa value that used to exist has been replaced by the ability for users
     to override all of the coeficcients which comprise an ISA. In other words, user can now specify
     overridden whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue,
     and adjustmentValue numbers.

     The following endpoint may serve as a potential, alternate, way to retrieve these values:
     http://localhost:9091/facility/{facilityId}/program/{programId}/stockRequirements.json
     */
    //Integer overriddenisa;
    Integer maxmonthsofstock;
    Double minmonthsofstock;
    Double eop;

}