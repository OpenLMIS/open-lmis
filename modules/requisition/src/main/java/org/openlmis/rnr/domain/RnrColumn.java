package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RnrColumn {

    private Integer id;
    private String name;
    private String description;
    private int position;
    private String label;
    private String defaultValue;
    private String dataSource;
    private String formula;
    private String indicator;
    private boolean used;
    private boolean visible;

    public RnrColumn() {
    }

    public RnrColumn(String name, String description, int position, String label, String defaultValue, String dataSource, String formula, String indicator, boolean isUsed, boolean isVisible) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.label = label;
        this.defaultValue = defaultValue;
        this.dataSource = dataSource;
        this.formula = formula;
        this.indicator = indicator;
        this.used = isUsed;
        this.visible = isVisible;
    }

}
