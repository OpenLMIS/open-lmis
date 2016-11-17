package org.openlmis.report.model;

import java.util.Date;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TracerDrugRequest {

    private String drugCode;

    private String drugName;

    private String province;

    private String district;

    private String facility;

    private Long quantity;

    private Date date;
}
