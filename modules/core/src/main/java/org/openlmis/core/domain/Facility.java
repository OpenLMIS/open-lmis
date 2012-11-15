package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility implements Importable {

    @ImportField(mandatory = true)
    private String code;
    @ImportField(mandatory = true)
    private String name;
    @ImportField(mandatory = true, type = "int")
    private int type;
    @ImportField(mandatory = true, type = "int")
    private int geographicZone;

    @Override
    public boolean validate() {
        return true;
    }
}
