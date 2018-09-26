package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.repository.KitProductRepository;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling KitProduct entity.
 */
@Service
@NoArgsConstructor
public class KitProductService {

    @Autowired
    private KitProductRepository kitProductRepository;

    public List<KitProduct> getByProductCode(String productCode){
        return kitProductRepository.getByProductCode(productCode);
    }
}
