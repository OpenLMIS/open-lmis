/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

// TODO use annotations for json serialization
@JsonSerialize(using = RnrColumnSourceSerializer.class)
@JsonDeserialize(using = RnrColumnSourceDeSerializer.class)
public enum RnRColumnSource {

    REFERENCE("R", "Reference Data"),
    USER_INPUT("U", "User Input"),
    CALCULATED("C", "Calculated");

    private final String code;

    private final String description;

    RnRColumnSource(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RnRColumnSource getValueOf(String value) {
        for (RnRColumnSource columnSource : RnRColumnSource.values()) {
            if (columnSource.code.equalsIgnoreCase(value)) return columnSource;
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
