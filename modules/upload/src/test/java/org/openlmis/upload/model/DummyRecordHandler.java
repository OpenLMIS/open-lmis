package org.openlmis.upload.model;

import lombok.Getter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;

import java.util.ArrayList;
import java.util.List;

public class DummyRecordHandler implements RecordHandler {

    @Getter
    private List<Importable> importedObjects = new ArrayList<Importable>();

    @Override
    public void execute(Importable importable) {
        this.importedObjects.add(importable);
    }
}
