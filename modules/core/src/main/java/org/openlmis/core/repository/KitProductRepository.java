package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.repository.mapper.KitProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class KitProductRepository {

    @Autowired
    KitProductMapper mapper;

    public void insert(KitProduct kitProduct) {
        mapper.insert(kitProduct);
    }
}
