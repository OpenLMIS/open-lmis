package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Role {
    private Integer id;
    private String name;
    private String description;
    private String modifiedBy;
    private List<Right> rights;

    public Role(String name, String description){
        this.name = name;
        this.description = description;
    }

    public void validate() {
        if(name==null || name.isEmpty()) throw new RuntimeException("Role can not be created without name.");
        if(rights==null || rights.isEmpty()) throw new RuntimeException("Role can not be created without any rights assigned to it.");
    }
}
