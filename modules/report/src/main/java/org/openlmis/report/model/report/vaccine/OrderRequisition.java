package org.openlmis.report.model.report.vaccine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequisition implements ReportData {

    private Integer maximumStock;
    private Integer reOrderLevel;
    private Integer bufferStock;
    private Integer stockOnHand;
    private String productName;
    private Integer quantityRequested;
    private String facilityName;
    private String periodName;

    private String REQUESTED_BY;
    private String CURRENT_DATE;

}
