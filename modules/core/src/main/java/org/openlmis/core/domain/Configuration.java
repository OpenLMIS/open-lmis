/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.lang.String;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
public class Configuration {

    private String key;
    private String value;
    private String name;
    private String description;
    private String valueType;
    private String valueOptions;
    private String displayOrder;
    private String groupName;
}
