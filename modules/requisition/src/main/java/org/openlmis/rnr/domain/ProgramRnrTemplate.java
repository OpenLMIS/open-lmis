package org.openlmis.rnr.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramRnrTemplate {

    private Map<String, RnrColumn> rnrColumnsMap = new HashMap<>();

    public ProgramRnrTemplate(List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            rnrColumnsMap.put(rnrColumn.getName(), rnrColumn);
        }
    }

    public RnrColumn rnrColumn(String name) {
        return rnrColumnsMap.get(name);
    }

    public boolean columnsVisible(String... rnrColumnNames) {
        boolean visible = true;
        for (String rnrColumnName : rnrColumnNames) {
            visible = visible && rnrColumnsMap.get(rnrColumnName).isVisible();
        }
        return visible;
    }


}
