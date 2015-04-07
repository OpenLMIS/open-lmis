package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportParameter;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor

public class PipelineExportParams
   extends BaseParam implements ReportParameter {

    private int programId;
    private int yearId;
    private int periodId;

}
