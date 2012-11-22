package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Product;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProductBuilder {

    public static final String PRODUCT_CODE = "P0001";

    public static final Property<Product, String> code = newProperty();

    public static final Instantiator<Product> product = new Instantiator<Product>() {
        @Override
        public Product instantiate(PropertyLookup<Product> lookup) {
            Product product = new Product();
            product.setCode(lookup.valueOf(code, PRODUCT_CODE));
            product.setPrimaryName("Product Primary Name");
            return product;
        }
    };

}
