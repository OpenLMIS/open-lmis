package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class RnrColumn {

    private static final Pattern formulaParser = Pattern.compile("\\w+");
    private Integer id;
    private String name;
    private String description;
    private int position;
    private String label;
    private String defaultValue;
    private String dataSource;
    private List<RnrColumnType> availableColumnTypes = new ArrayList<>();
    private RnrColumnType selectedColumnType;
    private String formula;

    public void setFormula(String formula) {
        this.formula = formula;
        parseFormulaForDependencies();
    }

    private String indicator;
    private boolean used;
    private boolean visible;
    private boolean mandatory;
    private List<RnrColumn> cyclicDependencies = new ArrayList<>();
    private Set<String> dependencies = new HashSet<>();

    public RnrColumn(String name, String description, int position, String label, String defaultValue, String dataSource, List<RnrColumnType> availableDataSources, String formula, String indicator, boolean isUsed, boolean isVisible, boolean mandatory) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.label = label;
        this.defaultValue = defaultValue;
        this.dataSource = dataSource;
        this.availableColumnTypes = availableDataSources;
        this.formula = formula;
        this.indicator = indicator;
        this.used = isUsed;
        this.visible = isVisible;
        this.mandatory = mandatory;
        parseFormulaForDependencies();
    }

    public void setSelectedColumnTypeString(String selectedColumnType) {
        this.selectedColumnType = RnrColumnType.getValueOf(selectedColumnType);
    }

    public void setAvailableColumnTypesString(String dataSourcesString) {
        String[] sources = dataSourcesString.split("/");
        for (String source : sources) {
            availableColumnTypes.add(RnrColumnType.getValueOf(source));
        }
    }

    public RnrColumnType getSelectedColumnType() {
        return (selectedColumnType == null && availableColumnTypes.size() != 0) ? availableColumnTypes.get(0) : selectedColumnType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    private void parseFormulaForDependencies() {
        Scanner formulaScanner = new Scanner(formula);
        while (formulaScanner.hasNext()) {
            if (formulaScanner.hasNext(formulaParser)) {
                dependencies.add(formulaScanner.next());
            } else {
               formulaScanner.next();
            }
            //dependencies.add(formulaScanner.next(formulaParser));
        }

    }

}
