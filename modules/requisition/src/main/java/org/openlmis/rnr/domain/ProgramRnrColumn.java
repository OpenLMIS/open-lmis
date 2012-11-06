package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ProgramRnrColumn {

    private Integer id;
    private Integer programId;
    // Verify if foreign key is set up correctly
    private Integer columnId;
    private boolean used;

    public ProgramRnrColumn() {
    }

    public ProgramRnrColumn(Integer programId, Integer columnId, boolean isUsed) {
        this.programId = programId;
        this.columnId = columnId;
        this.used = isUsed;
    }

}
