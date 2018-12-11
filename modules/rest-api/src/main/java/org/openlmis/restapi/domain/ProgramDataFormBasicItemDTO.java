package org.openlmis.restapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.moz.ProgramDataFormBasicItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDataFormBasicItemDTO {

    private String productCode;
    private Integer beginningBalance;
    private Integer quantityReceived;
    private Integer quantityDispensed;
    private Integer totalLossesAndAdjustments;
    private Integer stockInHand;
    private String expirationDate;

    public static ProgramDataFormBasicItemDTO prepareForRest(ProgramDataFormBasicItem programDataFormBasicItem) {
        return new ProgramDataFormBasicItemDTO(programDataFormBasicItem.getProductCode(),
                programDataFormBasicItem.getBeginningBalance(),
                programDataFormBasicItem.getQuantityReceived(),
                programDataFormBasicItem.getQuantityDispensed(),
                programDataFormBasicItem.getTotalLossesAndAdjustments(),
                programDataFormBasicItem.getStockInHand(), programDataFormBasicItem.getExpirationDate());
    }

}
