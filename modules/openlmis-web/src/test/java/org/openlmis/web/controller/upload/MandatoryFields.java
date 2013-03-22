/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller.upload;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
public class MandatoryFields implements Importable {
    @ImportField(mandatory = true, name = "field A")
    private String fieldA;

    @ImportField(mandatory = true, name = "field B")
    private String fieldB;

    @ImportField(mandatory = true, name = "nested field", nested = "nestedField")
    private NestedValidUploadType nestedValidUploadType;

}
