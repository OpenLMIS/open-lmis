package org.openlmis.upload.model;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

public class DummyImportable implements Importable {

    @Getter
    @Setter
    @ImportField(mandatory = true)
    String mandatoryField;

    @Getter
    @Setter
    @ImportField
    String optionalField;

    @Getter
    @Setter
    String nonAnnotatedField;

    @Override
    public boolean validate() {
        return true;
    }
}
