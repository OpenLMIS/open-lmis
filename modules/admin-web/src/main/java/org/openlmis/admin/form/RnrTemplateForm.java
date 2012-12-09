package org.openlmis.admin.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.List;

@Data
@NoArgsConstructor
public class RnrTemplateForm {

    RnrColumnList rnrColumns;

    List<RnRColumnSource> sources;

    public RnrTemplateForm(List<RnrColumn> rnrColumns, List<RnRColumnSource> sources) {
        this.rnrColumns = new RnrColumnList();
        for (RnrColumn rnrColumn : rnrColumns) {
            this.rnrColumns.add(rnrColumn);
        }
        this.sources = sources;
    }
}