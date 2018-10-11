package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.annotations.RequiredParam;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OverStockReportParam {

    @RequiredParam
    private Date startTime;
    @RequiredParam
    private Date endTime;

    private Integer provinceId;
    private Integer districtId;
    private Integer facilityId;
}