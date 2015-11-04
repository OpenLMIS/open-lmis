package org.openlmis.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.openlmis.core.domain.ISA;

import java.util.List;

@Data
@NoArgsConstructor
public class IsaDTO {

    Integer annualNeed;

    Integer quarterlyNeed;

    Double MinimumStock;

    Double MaximumStock;

    Double ReorderLevel;
    Integer isaValue;
    ISA isa;

    Double minMonthsOfStock;
    Double maxMonthsOfStock;
    Double eop;

    List<IsaDTO> isaCoefficients;

    private IsaDTO(Builder builder)
    {
        this.annualNeed = builder.annualNeed;
        this.quarterlyNeed = builder.quarterlyNeed;
        this.MinimumStock = builder.MinimumStock;
        this.MaximumStock = builder.MaximumStock;
        this.ReorderLevel = builder.ReorderLevel;
        this.isaValue = builder.isaValue;
    }

    public static class Builder
    {
        Integer annualNeed;

        Integer quarterlyNeed;

        Double MinimumStock;

        Double MaximumStock;

        Double ReorderLevel;

        int isaValue = 0;

    }
}
