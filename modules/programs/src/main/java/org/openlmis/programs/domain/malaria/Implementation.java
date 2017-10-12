package org.openlmis.programs.domain.malaria;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data
public class Implementation {
    private int id;

    @NotEmpty
    private String executor;
    @Valid
    private List<Treatment> treatments;
    private MalariaProgram malariaProgram;

    public Implementation(String executor, List<Treatment> treatments) {
        this.executor = executor;
        this.treatments = treatments;
    }
}
