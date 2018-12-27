package org.openlmis.restapi.service.integration;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.ProductIntegrationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductIntegrationFromFCService extends IntegrationFromFCService<ProductIntegrationDTO>{

    private static final String method = "product/getAllChangesV1";

    private ProductService productService;

    @Autowired
    public ProductIntegrationFromFCService(RestTemplate restTemplate,
                                           IntegrationFCConfig integrationFCConfig,
                                           ProductService productService) {
        super(restTemplate, integrationFCConfig);
        this.productService = productService;
    }

    @Override
    List<ProductIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, ProductIntegrationDTO[].class);
    }

    @Override
    void toDb(List<ProductIntegrationDTO> data) {
        List<Product> productsFromFc = convertProducts(data);
        for(Product productFromFc : productsFromFc) {
            Product product = productService.getByCode(productFromFc.getCode());
            if(product != null) {
                product.setActive(productFromFc.getActive());
                product.setPrimaryName(productFromFc.getPrimaryName());
            } else {
                product = productFromFc;
            }
            productService.save(product, false);
        }
    }

    private List<Product> convertProducts(List<ProductIntegrationDTO> productIntegrationDTOs) {
        return FluentIterable.from(productIntegrationDTOs).transform(new Function<ProductIntegrationDTO, Product>() {
            @Override
            public Product apply(ProductIntegrationDTO source) {
                Product target = new Product();
                BeanUtils.copyProperties(source, target);
                return target;
            }
        }).toList();
    }
}