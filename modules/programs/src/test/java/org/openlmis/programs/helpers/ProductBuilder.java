package org.openlmis.programs.helpers;

import org.openlmis.core.domain.Product;

import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class ProductBuilder {
    private String code = randomAlphanumeric(5);
    private String primaryName = randomAlphanumeric(10);
    private static ProductBuilder productBuilder;

    public static ProductBuilder fresh() {
        productBuilder = new ProductBuilder();
        return productBuilder;
    }

    public ProductBuilder with(Consumer<ProductBuilder> consumer){
        consumer.accept(productBuilder);
        return productBuilder;
    }

    public Product build() {
        Product product = new Product();
        product.setDefaultValuesForMandatoryFieldsIfNotExist();
        product.setCode(code);
        product.setPrimaryName(primaryName);
        return product;
    }
}
