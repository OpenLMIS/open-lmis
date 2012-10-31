package org.openlmis.admin.form;

import org.openlmis.rnr.domain.RnRColumn;

import java.util.ArrayList;
import java.util.List;

public class ProgramRnRTemplateForm {
    private List<RnRColumn> rnRColumns;

    public ProgramRnRTemplateForm(ArrayList<RnRColumn> rnRColumns) {
        this.rnRColumns = rnRColumns;
    }

    public List<RnRColumn> getRnRColumns() {
        return rnRColumns;
    }
}
