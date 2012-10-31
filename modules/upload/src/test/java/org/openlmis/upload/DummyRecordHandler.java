package org.openlmis.upload;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DummyRecordHandler implements RecordHandler{

    @Getter
    private List<Importable> importedObjects = new ArrayList<Importable>();

    @Override
    public void execute(Importable importable) {
        this.importedObjects.add(importable);
    }
}
