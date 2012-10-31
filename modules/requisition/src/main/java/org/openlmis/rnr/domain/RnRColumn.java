package org.openlmis.rnr.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class RnRColumn {

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

    public RnRColumn(){}

    public RnRColumn(Integer id, String name, String description, int position, String label, String defaultValue, String dataSource, String formula, String indicator, boolean isUsed, boolean isVisible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.position = position;
        this.label = label;
        this.defaultValue = defaultValue;
        this.dataSource = dataSource;
        this.formula = formula;
        this.indicator = indicator;
        this.used = isUsed;
        visible = isVisible;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (o.getClass() != getClass()) {
            return false;
        }
        RnRColumn rhs = (RnRColumn) o;
        return new EqualsBuilder()
                .append(position, rhs.position)
                .append(label, rhs.label)
                .append(name, rhs.name)
                .append(visible, rhs.visible)
                .append(dataSource, rhs.dataSource)
                .append(defaultValue, rhs.defaultValue)
                .append(description, rhs.description)
                .append(formula, rhs.formula)
                .append(indicator, rhs.indicator)
                .isEquals();
    }

    @Override
    public int hashCode() {
     return new HashCodeBuilder(13,17)
                .append(position)
                .append(label)
                .append(name)
                .append(visible)
                .append(dataSource)
                .append(defaultValue)
                .append(description)
                .append(formula)
                .append(indicator)
                .toHashCode();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPosition() {
        return position;
    }

    public String getLabel() {
        return label;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getFormula() {
        return formula;
    }

    public String getIndicator() {
        return indicator;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isVisible() {
        return visible;
    }

    public Integer getId() {
        return id;
    }
}
