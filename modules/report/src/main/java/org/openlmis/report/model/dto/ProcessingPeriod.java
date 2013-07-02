package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/29/13
 * Time: 4:10 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingPeriod {
    private Integer id;
    private String name;
    private String description;
    private Date startdate;
    private Date enddate;
}
