package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    private String description;

    public Role(String name, String description){
        this.name = name;
        this.description = description;
    }
}
