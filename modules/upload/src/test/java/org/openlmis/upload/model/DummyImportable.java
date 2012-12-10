package org.openlmis.upload.model;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
public class DummyImportable implements Importable {

    @ImportField(mandatory = true, name = "Mandatory String Field")
    String mandatoryStringField;

    @ImportField(mandatory = true, type = "int")
    int mandatoryIntField;

    @ImportField
    String optionalStringField;

    @ImportField(type = "int", name = "OPTIONAL INT FIELD")
    int optionalIntField;

    @ImportField(type = "Date")
    Date optionalDateField;

    @ImportField(type = "String", name = "OPTIONAL NESTED FIELD", nested ="code")
    DummyNestedField dummyNestedField;

    String nonAnnotatedField;

}

