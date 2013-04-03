/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
@NoArgsConstructor
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

  private String duplicateMessageKey;

  public AbstractModelPersistenceHandler(String duplicateMessageKey) {
    this.duplicateMessageKey = duplicateMessageKey;
  }

  @Override
  public void execute(Importable importable, int rowNumber, AuditFields auditFields) {
    final String rowNumberAsString = Integer.toString(rowNumber- 1);

    Importable existing = getExisting(importable);
    if (existing != null){
      BaseModel model = (BaseModel) existing;
      if (model.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
        throw new DataException(duplicateMessageKey);
      }
    }

    try {
      save(existing, importable, auditFields);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throw new DataException(new OpenLmisMessage("upload.record.error", "Incorrect data length", rowNumberAsString));
    } catch (DataException exception) {
      if(exception.getOpenLmisMessage()!= null){
        throw new DataException(new OpenLmisMessage("upload.record.error", exception.getOpenLmisMessage().getCode(), rowNumberAsString));
      }
    }
  }

  protected abstract Importable getExisting(Importable importable);

  protected abstract void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields);
}
