package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.repository.mapper.KitProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class KitProductRepository {

    @Autowired
    KitProductMapper mapper;

    public void insert(KitProduct kitProduct) {
        mapper.insert(kitProduct);
    }

    public List<KitProduct> getLatestKitProductByKitId(Long kitId, Date afterUpdatedTime) {
        return mapper.getLatestKitProductByKitId(kitId, afterUpdatedTime);
    }

    public List<KitProduct> getByKitId(Long kitId) {
        return mapper.getByKitId(kitId);
    }
}
