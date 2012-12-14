package org.openlmis.core.handler;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programProductPersistenceHandler")
public class ProgramProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProgramProductRepository programProductRepository;

    @Autowired
    public ProgramProductPersistenceHandler(ProgramProductRepository programProductRepository) {
        this.programProductRepository = programProductRepository;
    }

    @Override
    protected void save(Importable importable, String modifiedBy) {
        ProgramProduct programProduct = (ProgramProduct) importable;
        programProductRepository.insert(programProduct);
    }
}


