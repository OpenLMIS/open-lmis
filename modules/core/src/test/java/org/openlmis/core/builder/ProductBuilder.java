package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProductBuilder {

    public static final String PRODUCT_CODE = "P999";

    public static final Property<Product, String> code = newProperty();
    public static final Property<Product, Boolean> fullSupply = newProperty();
    public static final Property<Product, Integer> displayOrder=newProperty();
    private static final Integer nullInteger = null;

    public static final Instantiator<Product> product = new Instantiator<Product>() {
        @Override
        public Product instantiate(PropertyLookup<Product> lookup) {
            Product product = new Product();
            product.setCode(lookup.valueOf(code, PRODUCT_CODE));
            product.setFullSupply(lookup.valueOf(fullSupply, true));
            product.setActive(true);
            product.setAlternateItemCode("alternateItemCode");
            product.setManufacturer("Glaxo and Smith");
            product.setManufacturerCode("manufacturerCode");
            product.setManufacturerBarCode("manufacturerBarCode");
            product.setMohBarCode("mohBarCode");
            product.setGtin("gtin");
            product.setType("antibiotic");
            product.setPrimaryName("Primary Name");
            product.setFullName("TDF/FTC/EFV");
            product.setDisplayOrder(lookup.valueOf(displayOrder,nullInteger));
            product.setGenericName("Generic - TDF/FTC/EFV");
            product.setAlternateName("Alt - TDF/FTC/EFV");
            product.setDescription("is a med");
            product.setStrength("strength");
            product.setDosageUnitCode("mg");
            product.setDispensingUnit("Strip");
            product.setPackSize(10);
            product.setTracer(true);
            product.setPackRoundingThreshold(1);
            product.setRoundToZero(true);
            product.setFormCode("Tablet");
            DosageUnit productDosageUnit = new DosageUnit();
            productDosageUnit.setCode("Dosage Unit");
            product.setProductDosageUnit(productDosageUnit);
            ProductForm productForm = new ProductForm();
            productForm.setCode("form");
            product.setProductForm(productForm);
            return product;
        }
    };

}
