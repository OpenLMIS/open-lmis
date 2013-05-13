/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload;

import org.openlmis.upload.model.AuditFields;

public interface RecordHandler<I extends Importable> {

  public void execute(I importable, int rowNumber, AuditFields auditFields);

  public void postProcess();
}
