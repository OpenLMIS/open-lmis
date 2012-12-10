package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RnrColumn {

    private Integer id;
    private String name;
    private String description;
    private int position;
    private String label;
    private RnRColumnSource source;
    private Boolean sourceConfigurable;
    private String formula;
    private String indicator;
    private boolean used;
    private boolean visible;
    private boolean mandatory;

    @SuppressWarnings(value = "unused")
    public void setSourceString(String sourceString) {
        this.source = RnRColumnSource.getValueOf(sourceString);
    }

}
