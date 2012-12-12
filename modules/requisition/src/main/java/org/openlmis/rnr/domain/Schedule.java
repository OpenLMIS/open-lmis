package org.openlmis.rnr.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Schedule {

    Long id;

    String code;
    String name;
    String description;

}
