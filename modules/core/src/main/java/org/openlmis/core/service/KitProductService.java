package org.openlmis.core.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.KitProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class KitProductService {

    @Autowired
    private KitProductRepository repository;

    public void insert(KitProduct kitProduct) {
        repository.insert(kitProduct);
    }

    public List<Product> getProductsForKitAfterUpdatedTime(Long kitId, Date afterUpdatedTime) {
        List<KitProduct> kitProducts = afterUpdatedTime == null ?
                repository.getByKitId(kitId) : repository.getLatestKitProductByKitId(kitId, afterUpdatedTime);

        return FluentIterable.from(kitProducts).transform(new Function<KitProduct, Product>() {
            @Override
            public Product apply(KitProduct kitProduct) {
                Product product = kitProduct.getProduct();
                product.setQuantityInKit(kitProduct.getQuantity());
                return product;
            }
        }).toList();
    }
}
