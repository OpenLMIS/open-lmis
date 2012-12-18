package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RnrColumn {

    private Integer id;
    private String name;
    private int position;
    private RnRColumnSource source;
    private Boolean sourceConfigurable;
    private String label;
    private String formula;
    private String indicator;
    private boolean used;
    private boolean visible;
    private boolean mandatory;
    private String description;
    private boolean formulaValidated = true;

    @SuppressWarnings(value = "unused")
    public void setSourceString(String sourceString) {
        this.source = RnRColumnSource.getValueOf(sourceString);
    }

}
