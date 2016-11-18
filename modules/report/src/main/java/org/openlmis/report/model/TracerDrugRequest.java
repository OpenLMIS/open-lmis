package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TracerDrugRequest implements ReportData {

    private String drugCode;

    private String drugName;

    private String province;

    private String district;

    private String facility;

    private Long quantity;

    private Date date;
}
