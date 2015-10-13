package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccineOrderRequisitionColumns extends BaseModel {

    String name;
    String description;
    Integer displayOrder;
    String label;
    String indicator;
    Boolean mandatory;

    Long programId;
    Long masterColumnId;

    Boolean visible;


}
