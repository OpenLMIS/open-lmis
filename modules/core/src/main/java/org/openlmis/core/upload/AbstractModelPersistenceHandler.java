package org.openlmis.core.upload;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

  @Override
  public void execute(Importable importable, int rowNumber, String modifiedBy) {
    final String rowNumberAsString = Integer.toString(rowNumber- 1);
    try {
      save(importable, modifiedBy);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throw new DataException(new OpenLmisMessage("upload.record.error", "Incorrect data length", rowNumberAsString));
    } catch (DataException exception) {
      if(exception.getOpenLmisMessage()!= null){
        throw new DataException(new OpenLmisMessage("upload.record.error", exception.getOpenLmisMessage().getCode(), rowNumberAsString));
      }
    }
  }

  protected abstract void save(Importable modelClass, String modifiedBy);
}
