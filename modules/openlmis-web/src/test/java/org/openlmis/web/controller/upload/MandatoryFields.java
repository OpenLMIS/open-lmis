package org.openlmis.web.controller.upload;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
public class MandatoryFields implements Importable {
    @ImportField(mandatory = true, name = "field A")
    private String fieldA;

    @ImportField(mandatory = true, name = "field B")
    private String fieldB;

    @ImportField(mandatory = true, name = "nested field", nested = "nestedField")
    private NestedValidUploadType nestedValidUploadType;

}
