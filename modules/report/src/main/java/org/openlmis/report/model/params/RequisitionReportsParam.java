package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.annotations.RequiredParam;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionReportsParam {
    @RequiredParam
    private Date startTime;
    @RequiredParam
    private Date endTime;
    @RequiredParam
    private List<Integer> programIds;

    private Integer provinceId;
    private Integer districtId;
    private Integer facilityId;
}