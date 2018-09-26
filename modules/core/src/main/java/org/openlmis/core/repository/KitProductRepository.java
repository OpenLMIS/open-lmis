package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.repository.mapper.KitProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * KitProductRepository is Repository class for KitProduct related database operations.
 */
@Component
@NoArgsConstructor
public class KitProductRepository {

   @Autowired
   private KitProductMapper kitProductMapper;

    public List<KitProduct> getByProductCode(String productCode){
        //return new KitProduct("SCOD10-AL","08O05",60);
        return kitProductMapper.getByProductCode(productCode);
    }
}
