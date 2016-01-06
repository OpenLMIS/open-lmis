package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Kit;
import org.openlmis.core.domain.Product;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.Property.newProperty;

public class KitBuilder {
    public static final Property<Kit, String> code = newProperty();
    public static final Property<Kit, String> primaryName = newProperty();
    public static final Property<Kit, Long> kitId = newProperty();
    public static final Property<Kit, List<Product>> products = newProperty();

    public static final Instantiator<Kit> defaultKit = new Instantiator<Kit>() {
        @Override
        public Kit instantiate(PropertyLookup<Kit> lookup) {
            Kit kit = new Kit();
            kit.setId(lookup.valueOf(kitId, 1L));
            kit.setCode(lookup.valueOf(code, "Default Kit Code"));
            kit.setPrimaryName(lookup.valueOf(primaryName, "default kit primary name"));
            kit.setProducts(lookup.valueOf(products, new ArrayList<Product>()));
            return kit;
        }
    };
}
