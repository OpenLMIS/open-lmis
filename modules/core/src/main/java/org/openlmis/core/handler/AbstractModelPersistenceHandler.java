package org.openlmis.core.handler;

import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

    @Override
    public void execute(Importable importable, int rowNumber) {
        try {
            save(importable);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException(String.format("Duplicate Product Code found in Record No. %d", rowNumber - 1));
        } catch (DataIntegrityViolationException foreignKeyException) {
            if (foreignKeyException.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException(String.format("Missing Reference data of Record No. %d", rowNumber - 1));
            }
        }
    }

    protected abstract void save(Importable modelClass);
}
