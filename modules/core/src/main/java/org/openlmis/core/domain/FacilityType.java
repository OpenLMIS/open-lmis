package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class FacilityType {

    private String code;

    private String name;

    private String description;

    private Date modifiedDate;

    private long modifiedBy;

}
