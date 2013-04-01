package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReport implements ReportData {
    private String code;
    private String facilityName;
    private String facilityType;

}
