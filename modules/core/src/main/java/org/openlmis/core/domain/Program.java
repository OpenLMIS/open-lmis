package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Program {

    // make this primary key
    private Integer id;
    private String name;
    private String description;

    public Program() {
    }

    public Program(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
