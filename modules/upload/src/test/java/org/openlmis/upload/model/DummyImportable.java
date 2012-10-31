package org.openlmis.upload.model;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

public class DummyImportable implements Importable {

    @Getter
    @Setter
    @ImportField(mandatory = true)
    String mandatoryStringField;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "int")
    int mandatoryIntField;

    @Getter
    @Setter
    @ImportField
    String optionalStringField;

    @Getter
    @Setter
    @ImportField(type = "int")
    int optionalIntField;

    @Getter
    @Setter
    String nonAnnotatedField;

    @Override
    public boolean validate() {
        return true;
    }
}
