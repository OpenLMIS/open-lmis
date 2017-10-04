package org.openlmis.programs.domain.malaria;

import lombok.Data;

import java.util.List;

@Data
public class Implementation {
    private int id;
    private String executor;
    private List<Treatment> treatments;
    private MalariaProgram malariaProgram;

    public Implementation(String executor, List<Treatment> treatments) {
        this.executor = executor;
        this.treatments = treatments;
    }
}
