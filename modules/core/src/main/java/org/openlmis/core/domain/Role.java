package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role {
    int id;
    String name;
    String description;

    public Role(String name, String description){
        this.name = name;
        this.description = description;
    }
}
