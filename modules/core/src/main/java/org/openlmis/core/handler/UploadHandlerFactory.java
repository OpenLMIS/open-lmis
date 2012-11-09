package org.openlmis.core.handler;

import org.openlmis.upload.RecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UploadHandlerFactory {
    private ProductImportHandler productImportHandler;
    private Map<String, RecordHandler> map;

    private static final String PRODUCT = "product";

    @Autowired
    public UploadHandlerFactory(ProductImportHandler productImportHandler){
        this.productImportHandler = productImportHandler;
        map = new HashMap();
        initializeMappers();
    }

    private void initializeMappers() {
        map.put(PRODUCT, productImportHandler);
    }

    public RecordHandler getHandler(String model) {
        return map.get(model);
    }
}
