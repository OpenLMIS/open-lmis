package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ProgramRnrColumn {

    private Integer id;
    private String programCode;
    // Verify if foreign key is set up correctly
    private Integer columnId;
    private boolean used;

}
