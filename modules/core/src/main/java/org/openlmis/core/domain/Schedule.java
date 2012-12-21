package org.openlmis.core.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Schedule {

    Integer id;

    String code;
    String name;
    String description;

}
