package org.openlmis.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.stockmanagement.domain.StockCard;

import java.util.Date;

/**
 * Created by chrispinus on 10/29/15.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class OrderRequisitionStockCardDTO extends StockCard {

    Integer maxmonthsofstock;
    Double minmonthsofstock;
    Double eop;
    Double whoRatio;
    Integer dosesPerYear;
    Double wastageFactor;
    Double bufferPercentage;
    Integer minimumValue;
    Integer maximumValue;
    Integer adjustmentValue;
}
