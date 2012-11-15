package org.openlmis.core.handler;

import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.springframework.stereotype.Component;

@Component("AbstractModelPersistenceHandler")
public abstract class AbstractModelPersistenceHandler implements RecordHandler<Importable> {

    @Override
    public void execute(Importable importable, int rowNumber) {
        try {
            save(importable);
        }catch (RuntimeException exception){
            throw new RuntimeException(String.format("%s in Record No. %d",exception.getMessage(), rowNumber - 1));
        }
    }

    protected abstract void save(Importable modelClass);
}
