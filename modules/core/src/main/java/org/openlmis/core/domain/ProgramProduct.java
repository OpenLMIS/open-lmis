package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramProduct {

    private String id;

    private String programCode;

    private String productCode;

    private Date modifiedDate;

    private long modifiedBy;

}
