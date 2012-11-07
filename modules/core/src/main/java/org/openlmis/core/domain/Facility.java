package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Facility {

    private String code;
    private String name;
    private int type;
    private int geographicZone;

    public Facility() {
    }

}
