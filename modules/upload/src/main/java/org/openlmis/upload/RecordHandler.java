package org.openlmis.upload;

import org.openlmis.upload.model.AuditFields;

public interface RecordHandler<I extends Importable> {

    public void execute(I importable, int rowNumber, AuditFields auditFields);
}
