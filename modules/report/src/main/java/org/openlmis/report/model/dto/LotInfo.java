package org.openlmis.report.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LotInfo {
    private String lotNumber;
    private Date expiryDate;
    private Integer stockOnHandOfLot;
    private String expiryDateLocalTime;

    public LotInfo(String lotNumber, Date expiryDate, Integer stockOnHandOfLot) {
        this.lotNumber = lotNumber;
        this.expiryDate = expiryDate;
        this.stockOnHandOfLot = stockOnHandOfLot;
        this.expiryDateLocalTime = DateUtil.formatDate(expiryDate);
    }
}
