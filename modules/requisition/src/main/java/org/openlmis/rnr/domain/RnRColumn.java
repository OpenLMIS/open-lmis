package org.openlmis.rnr.domain;

public class RnRColumn {

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

    public RnRColumn(String name, String description, int position, String label, String defaultValue, String dataSource, String formula, String indicator, boolean isUsed, boolean isVisible) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RnRColumn rnRColumn = (RnRColumn) o;

        if (position != rnRColumn.position) return false;
        if (used != rnRColumn.used) return false;
        if (visible != rnRColumn.visible) return false;
        if (dataSource != null ? !dataSource.equals(rnRColumn.dataSource) : rnRColumn.dataSource != null) return false;
        if (defaultValue != null ? !defaultValue.equals(rnRColumn.defaultValue) : rnRColumn.defaultValue != null)
            return false;
        if (description != null ? !description.equals(rnRColumn.description) : rnRColumn.description != null)
            return false;
        if (formula != null ? !formula.equals(rnRColumn.formula) : rnRColumn.formula != null) return false;
        if (indicator != null ? !indicator.equals(rnRColumn.indicator) : rnRColumn.indicator != null) return false;
        if (label != null ? !label.equals(rnRColumn.label) : rnRColumn.label != null) return false;
        if (name != null ? !name.equals(rnRColumn.name) : rnRColumn.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        result = 31 * result + (indicator != null ? indicator.hashCode() : 0);
        result = 31 * result + (used ? 1 : 0);
        result = 31 * result + (visible ? 1 : 0);
        return result;
    }


    @Override
    public String toString() {
        return "RnRColumn{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", label='" + label + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", formula='" + formula + '\'' +
                ", indicator='" + indicator + '\'' +
                ", used=" + used +
                ", visible=" + visible +
                '}';
    }
}
