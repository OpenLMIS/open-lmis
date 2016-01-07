package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.KitProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class KitProductBuilder {
    public static final Property<KitProduct, String> kitCode = newProperty();
    public static final Property<KitProduct, String> productCode = newProperty();
    public static final Property<KitProduct, Integer> quantity = newProperty();

    public static final Instantiator<KitProduct> defaultKit = new Instantiator<KitProduct>() {
        @Override
        public KitProduct instantiate(PropertyLookup<KitProduct> lookup) {
            KitProduct kitProduct = new KitProduct();
            kitProduct.setKitCode(lookup.valueOf(kitCode, "Default Kit Code"));
            kitProduct.setProductCode(lookup.valueOf(productCode, "default product code"));
            kitProduct.setQuantity(lookup.valueOf(quantity, 100));
            return kitProduct;
        }
    };
}
