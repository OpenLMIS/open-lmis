/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.form;

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