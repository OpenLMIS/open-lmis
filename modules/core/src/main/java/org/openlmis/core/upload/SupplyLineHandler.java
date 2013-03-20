package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@NoArgsConstructor
public class SupplyLineHandler extends AbstractModelPersistenceHandler {

    SupplyLineRepository supplyLineRepository;

    @Autowired
    public SupplyLineHandler(SupplyLineRepository supplyLineRepository){
        this.supplyLineRepository = supplyLineRepository;
    }

    @Override
    protected void save(Importable modelClass, AuditFields auditFields) {
        SupplyLine supplyLine = (SupplyLine) modelClass;
        supplyLine.setModifiedBy(auditFields.getUser());
        supplyLine.setModifiedDate(new Date());
        supplyLineRepository.insert(supplyLine);
    }
}