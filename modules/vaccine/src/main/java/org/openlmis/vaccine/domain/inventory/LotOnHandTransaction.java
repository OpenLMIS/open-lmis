package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LotOnHandTransaction extends BaseModel {

    Lot lot;

    Long lotId;

    Long stockCardId;

    Long quantity;

    Integer vvmStatus; //used for physical count

    List<AdjustmentReason> adjustmentReasons;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = DateDeserializer.class)
    Date effectiveDate;
}
