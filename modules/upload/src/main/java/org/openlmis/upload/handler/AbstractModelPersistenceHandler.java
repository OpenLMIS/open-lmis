package org.openlmis.upload.handler;

import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

    @Override
    public void execute(Importable importable, int rowNumber, String modifiedBy) {
        try {
            save(importable, modifiedBy);
        } catch (RuntimeException exception) {
            throw new UploadException(String.format("%s in Record No. %d", exception.getMessage(), rowNumber - 1));
        }
    }

    protected abstract void save(Importable modelClass, String modifiedBy);
}
