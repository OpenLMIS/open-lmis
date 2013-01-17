package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Program implements BaseModel {

    private Integer id;
    private String code;
    private String name;
    private String description;
    private Boolean active;

}
