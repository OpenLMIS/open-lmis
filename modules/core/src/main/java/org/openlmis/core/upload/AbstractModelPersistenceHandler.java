/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

/**
 * AbstractModelPersistenceHandler is a base class used for persisting each record of the uploaded file.
 */
@Component
@NoArgsConstructor
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

  @Autowired
  MessageService messageService;

  protected abstract BaseModel getExisting(BaseModel record);

  protected abstract void save(BaseModel record);

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
      if (auditFields.getCurrentTimestamp().equals(existing.getModifiedDate())) {
        throw new DataException(getMessageKey());
      }
    }
  }

  @Override
  public void postProcess(AuditFields auditFields) {
  }

}
