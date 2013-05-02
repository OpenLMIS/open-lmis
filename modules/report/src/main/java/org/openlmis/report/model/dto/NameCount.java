package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:42 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameCount implements ReportData {

    private String name;
    private String count;

}
