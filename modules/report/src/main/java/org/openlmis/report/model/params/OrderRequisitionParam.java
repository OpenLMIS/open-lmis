package org.openlmis.report.model.params;

import lombok.*;
import org.openlmis.report.model.ReportParameter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequisitionParam extends BaseParam implements ReportParameter {


    private Long id;
    private Long facilityId;
    private Long programId;
    private String program;
    private String facilityName;


   /* @Override
    public String toString() {

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Program: ").append(this.program).append("\n").
                append("Facility: ").append(this.facilityName).append(" - ");

        return filtersValue.toString();
    }*/

}
