/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.helper;

import org.openlmis.core.domain.BaseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommaSeparator<T extends BaseModel> {

    public String commaSeparateIds(List<T> list) {
        List<Long> ids = new ArrayList<>();

        for (T t : list) {
            ids.add(t.getId());
        }
        return ids.toString().replace("[", "{").replace("]", "}");
    }
}
