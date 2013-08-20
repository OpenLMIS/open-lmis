package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockedOutReport implements ReportData {

    private String facilitycode;
    private String facility;
    private String product;
    private String facilitytypename;
    private String location;
    private String supplyingFacility;
}
