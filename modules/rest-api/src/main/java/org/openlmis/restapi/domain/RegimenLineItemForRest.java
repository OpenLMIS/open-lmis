package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RegimenLineItem;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode()
public class RegimenLineItemForRest extends RegimenLineItem {
    private String code;
    private String name;
    private Integer patientsOnTreatment;
    private String categoryName;

    public static RegimenLineItemForRest convertFromRegimenLineItem(RegimenLineItem regimenLineItem) {
        return new RegimenLineItemForRest(regimenLineItem.getCode(), regimenLineItem.getName(), regimenLineItem.getPatientsOnTreatment(), regimenLineItem.getCategory().getName());
    }
}
