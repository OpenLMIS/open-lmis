package org.openlmis.programs.domain.malaria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
public class Implementation {
    private int id;

    @NotEmpty
    private String executor;
    @Valid
    private List<Treatment> treatments;
    @JsonIgnore
    private MalariaProgram malariaProgram;

    public Implementation(String executor, List<Treatment> treatments) {
        this.executor = executor;
        this.treatments = treatments;
    }
}
