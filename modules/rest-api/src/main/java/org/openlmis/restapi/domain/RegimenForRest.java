package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.rnr.domain.RegimenLineItem;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonDeserialize
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode()
public class RegimenForRest extends Regimen {
    private String categoryName;

    public RegimenForRest(String name, String code, Long programId, Boolean active,
                          RegimenCategory category, String categoryName, Integer displayOrder,
                          boolean isCustom, boolean skipped) {
        super(name, code, programId, active, category, displayOrder, isCustom, skipped);
        this.categoryName = categoryName;
    }

    public static RegimenForRest convertFromRegimenLineItem(Regimen regimen) {
        return new RegimenForRest(regimen.getName(), regimen.getCode(), regimen.getProgramId(),
                regimen.getActive(), regimen.getCategory(), regimen.getCategory().getName(), regimen.getDisplayOrder(),
                regimen.isCustom(), regimen.isSkipped());
    }
}
