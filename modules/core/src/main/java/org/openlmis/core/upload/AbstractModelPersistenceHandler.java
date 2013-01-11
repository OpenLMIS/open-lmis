package org.openlmis.core.upload;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

  @Override
  public void execute(Importable importable, int rowNumber, String modifiedBy) {
    try {
      save(importable, modifiedBy);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throw new UploadException(String.format("%s in Record No. %d", "Incorrect data length", rowNumber - 1));
    } catch (DataException exception) {
      if(exception.getOpenLmisMessage()!= null){
        throw new UploadException("upload.record.error", exception.getOpenLmisMessage().getCode(), Integer.toString(rowNumber -1));
      }
      throw new UploadException("upload.record.error", exception.getMessage(), Integer.toString(rowNumber -1));
    }
  }

  protected abstract void save(Importable modelClass, String modifiedBy);
}
