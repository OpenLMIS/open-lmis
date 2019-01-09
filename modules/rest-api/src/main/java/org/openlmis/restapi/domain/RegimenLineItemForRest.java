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
    private String categoryName;

    public RegimenLineItemForRest(String code, String name, Integer patientsOnTreatment, String categoryName, Integer hf, Integer chw) {
        this.setCode(code);
        this.setName(name);
        this.setPatientsOnTreatment(patientsOnTreatment);
        this.categoryName = categoryName;
        this.setHf(hf);
        this.setChw(chw);
    }

    public static RegimenLineItemForRest convertFromRegimenLineItem(RegimenLineItem regimenLineItem) {
        return new RegimenLineItemForRest(regimenLineItem.getCode(), regimenLineItem.getName(),
                regimenLineItem.getPatientsOnTreatment(), regimenLineItem.getCategory().getName(),
                regimenLineItem.getHf(), regimenLineItem.getChw());
    }
}
