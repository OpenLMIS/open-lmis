package org.openlmis.report.model.dto;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RegimenCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Regimen {
    private int id;
    private String name;
    private String code;
    private Long programId;
    private Boolean active;
    private RegimenCategory category;
    private Integer displayOrder;


}
