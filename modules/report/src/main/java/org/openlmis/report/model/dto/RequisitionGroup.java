package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/29/13
 * Time: 4:10 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionGroup {
    private Integer id;
    private String name;
    private String code;

}
