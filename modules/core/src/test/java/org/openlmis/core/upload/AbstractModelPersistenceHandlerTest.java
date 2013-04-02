/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import static org.mockito.Mockito.mock;

public class AbstractModelPersistenceHandlerTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldAppendRowNumberToExceptionMessage() throws Exception {
    AbstractModelPersistenceHandler handler = new AbstractModelPersistenceHandler() {

      @Override
      protected Importable getExisting(Importable importable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
        throw new DataException("error");
      }

      @Override
      protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
      }


    };

    Importable importable = mock(Importable.class);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("code: upload.record.error, params: { error; 1 }");

    handler.execute(importable, 2, new AuditFields(1,null));
  }
}
