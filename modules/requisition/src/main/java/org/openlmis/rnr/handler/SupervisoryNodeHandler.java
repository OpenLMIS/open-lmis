package org.openlmis.rnr.handler;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.openlmis.rnr.service.SupervisoryNodeService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component("supervisoryNodeHandler")
public class SupervisoryNodeHandler extends AbstractModelPersistenceHandler {

    private SupervisoryNodeService supervisoryNodeService;

    @Autowired
    public SupervisoryNodeHandler(SupervisoryNodeService supervisoryNodeService) {
        this.supervisoryNodeService = supervisoryNodeService;
    }

    @Override
    protected void save(Importable modelClass, String modifiedBy) {
        SupervisoryNode supervisoryNode = (SupervisoryNode) modelClass;
        supervisoryNode.setModifiedBy(modifiedBy);
        supervisoryNodeService.save(supervisoryNode);
    }
}
