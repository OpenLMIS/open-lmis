package org.openlmis.core.handler;

import lombok.NoArgsConstructor;
import org.openlmis.upload.RecordHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@NoArgsConstructor
public class UploadHandlerFactory {

    @Resource
    private Map<String, RecordHandler> persistenceHandlerMap;

    public UploadHandlerFactory(Map persistenceHandlerMap){
        this.persistenceHandlerMap = persistenceHandlerMap;
    }

    public RecordHandler getHandler(String model) {
        return persistenceHandlerMap.get(model);
    }
}
