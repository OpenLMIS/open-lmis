/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.model;

import lombok.Getter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;

import java.util.ArrayList;
import java.util.List;

public class DummyRecordHandler implements RecordHandler<DummyImportable> {

    @Getter
    private List<Importable> importedObjects = new ArrayList<Importable>();

    @Override
    public void execute(DummyImportable importable, int rowNumber, AuditFields auditFields) {
        this.importedObjects.add(importable);
    }
}
