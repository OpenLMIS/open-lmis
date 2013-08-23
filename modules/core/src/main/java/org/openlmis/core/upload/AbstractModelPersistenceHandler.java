/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

  @Autowired
  MessageService messageService;

  @Getter
  @Setter
  String messageKey;

  @Override
  public void execute(Importable importable, int rowNumber, AuditFields auditFields) {
    BaseModel currentRecord = (BaseModel) importable;
    BaseModel existing = getExisting(currentRecord);

    try {
      throwExceptionIfProcessedInCurrentUpload(auditFields, existing);
      currentRecord.setModifiedBy(auditFields.getUser());
      currentRecord.setModifiedDate(auditFields.getCurrentTimestamp());
      if (existing != null) {
        currentRecord.setId(existing.getId());
      } else {
        currentRecord.setCreatedBy(auditFields.getUser());
      }

      save(currentRecord);

    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throwException("upload.record.error", "error.incorrect.length", rowNumber);
    } catch (DataException exception) {
      throwException("upload.record.error", exception.getOpenLmisMessage().getCode(), rowNumber);
    }
  }

  private void throwException(String key1, String key2, int rowNumber) {
    String param1 = messageService.message(key2);
    String param2 = Integer.toString(rowNumber - 1);
    throw new DataException(new OpenLmisMessage(messageService.message(key1, param1, param2)));
  }

  private void throwExceptionIfProcessedInCurrentUpload(AuditFields auditFields, BaseModel existing) {
    if (existing != null) {
      if (existing.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
        throw new DataException(getMessageKey());
      }
    }
  }

  @Override
  public void postProcess(AuditFields auditFields) {
  }

  protected abstract BaseModel getExisting(BaseModel record);

  protected abstract void save(BaseModel record);

}
