package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("supervisoryNodeHandler")
@NoArgsConstructor
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
        supervisoryNode.setModifiedDate(new Date());
        supervisoryNodeService.save(supervisoryNode);
    }
}
