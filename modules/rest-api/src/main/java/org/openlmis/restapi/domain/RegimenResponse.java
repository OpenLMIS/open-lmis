package org.openlmis.restapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openlmis.rnr.domain.RegimenLineItem;

@AllArgsConstructor
@Data
public class RegimenResponse {
    private String code;
    private String name;
    private Integer patientsOnTreatment;
    private String categoryCode;

    public static RegimenResponse convertFromRegimenLineItem(RegimenLineItem regimenLineItem) {
        return new RegimenResponse(regimenLineItem.getCode(), regimenLineItem.getName(), regimenLineItem.getPatientsOnTreatment(), regimenLineItem.getCategory().getCode());
    }
}
