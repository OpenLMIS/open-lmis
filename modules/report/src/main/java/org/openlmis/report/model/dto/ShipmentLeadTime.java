package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 2/28/14
 * Time: 4:20 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentLeadTime {
    private String code;
    private String name;
    private Integer leadTime;
}
