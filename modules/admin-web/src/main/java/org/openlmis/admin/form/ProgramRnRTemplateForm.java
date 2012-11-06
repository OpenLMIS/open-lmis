package org.openlmis.admin.form;

import org.openlmis.rnr.domain.RnrColumn;

import java.util.ArrayList;
import java.util.List;

public class ProgramRnRTemplateForm {

    private List<RnrColumn> rnrColumns;

    public ProgramRnRTemplateForm(ArrayList<RnrColumn> rnrColumns) {
        this.rnrColumns = rnrColumns;
    }

    public List<RnrColumn> getRnrColumns() {
        return rnrColumns;
    }

}
