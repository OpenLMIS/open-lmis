package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.annotations.RequiredParam;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NonSubmittedRequisitionReportsParam {
    @RequiredParam
    private Date startTime;

    @RequiredParam
    private Date endTime;

    @RequiredParam
    private Integer facilityId;

    @RequiredParam
    private Integer programId;
}