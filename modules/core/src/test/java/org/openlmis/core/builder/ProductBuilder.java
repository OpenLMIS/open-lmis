package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Product;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProductBuilder {

    public static final String PRODUCT_CODE = "P999";

    public static final Property<Product, String> code = newProperty();
    public static final Property<Product, Boolean> fullSupply = newProperty();

    public static final Instantiator<Product> product = new Instantiator<Product>() {
        @Override
        public Product instantiate(PropertyLookup<Product> lookup) {
            Product product = new Product();
            product.setCode(lookup.valueOf(code, PRODUCT_CODE));
            product.setFullSupply(lookup.valueOf(fullSupply, true));
            product.setAlternateItemCode("alternateItemCode");
            product.setManufacturer("Glaxo and Smith");
            product.setManufacturerCode("manufacturerCode");
            product.setManufacturerBarCode("manufacturerBarCode");
            product.setMohBarCode("mohBarCode");
            product.setGtin("gtin");
            product.setType("antibiotic");
            product.setPrimaryName("antibiotic");
            product.setFullName("TDF/FTC/EFV");
            product.setGenericName("Generic - TDF/FTC/EFV");
            product.setAlternateName("Alt - TDF/FTC/EFV");
            product.setDescription("is a med");
            product.setStrength("strength");
            product.setDispensingUnit("Strip");
            product.setDispensingUnit("Strip");
            product.setPackSize(10);
            product.setActive(true);
            product.setTracer(true);
            product.setPackRoundingThreshold(1);
            product.setRoundToZero(true);
            product.setForm(1);
            product.setDosageUnit(1);
            return product;
        }
    };

}
