package org.openlmis.core.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Schedule {
    private Integer id;

    private String code;
    private String name;
    private String description;
    private Integer modifiedBy;
    private Date modifiedDate;

    public Schedule(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
